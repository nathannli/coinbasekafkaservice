package com.nathannli.coinbasekafkaservice.service;

import com.nathannli.coinbasekafkaservice.config.Properties;
import com.nathannli.coinbasekafkaservice.logging.AppLogger;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.JsonNode;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@Service
public class WebSocketService {
    private final AppLogger logger = new AppLogger(WebSocketService.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Properties properties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketService(
            KafkaTemplate<String, String> kafkaTemplate,
            Properties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.properties = properties;
    }

    public void start() {
        String wsUrl = properties.getWsUrl();
        logger.info("Starting WebSocket client for {} (topic={}, product_ids={})",
                wsUrl, properties.getTopic(), properties.getProductIds());

        HttpClient client = HttpClient.newHttpClient();

        client.newWebSocketBuilder()
                .buildAsync(URI.create(wsUrl), new WebSocket.Listener() {
                    private final StringBuilder buffer = new StringBuilder();

                    @Override
                    public void onOpen(WebSocket webSocket) {
                        logger.info("WebSocket connected to {}", wsUrl);
                        try {
                            String subscribe = objectMapper.writeValueAsString(Map.of(
                                    "type", "subscribe",
                                    "product_ids", properties.getProductIds(),
                                    "channels", List.of("matches")));
                            webSocket.sendText(subscribe, true);
                            logger.debug("Sent subscribe: {}", subscribe);
                        } catch (Exception e) {
                            logger.error("Failed to send subscribe", e);
                            throw new RuntimeException(e);
                        }
                        webSocket.request(1);
                    }

                    @Override
                    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                        buffer.append(data);
                        if (last) {
                            String msg = buffer.toString();
                            buffer.setLength(0);

                            try {
                                JsonNode json = objectMapper.readTree(msg);
                                JsonNode typeNode = json.path("type");
                                if (!typeNode.isMissingNode() && "match".equals(typeNode.asText())) {
                                    String key = json.path("product_id").asText();
                                    kafkaTemplate.send(properties.getTopic(), key, msg);
                                    logger.debug("Sent match to Kafka: product_id={}", key);
                                }
                            } catch (Exception e) {
                                logger.warn("Error processing message: {}", e.getMessage());
                            }
                        }

                        webSocket.request(1);
                        return null;
                    }

                    @Override
                    public void onError(WebSocket webSocket, Throwable error) {
                        logger.error("WebSocket error", error);
                    }

                    @Override
                    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                        logger.warn("WebSocket closed: statusCode={}, reason={}", statusCode, reason);
                        return null;
                    }
                });
    }
}
