package com.nathannli.coinbasekafkaservice.service;

import com.nathannli.coinbasekafkaservice.config.Properties;

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
        HttpClient client = HttpClient.newHttpClient();

        client.newWebSocketBuilder()
                .buildAsync(URI.create(properties.getWsUrl()), new WebSocket.Listener() {
                    private final StringBuilder buffer = new StringBuilder();

                    @Override
                    public void onOpen(WebSocket webSocket) {
                        try {
                            String subscribe = objectMapper.writeValueAsString(Map.of(
                                    "type", "subscribe",
                                    "product_ids", properties.getProductIds(),
                                    "channels", List.of("matches")));
                            webSocket.sendText(subscribe, true);
                        } catch (Exception e) {
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
                                if ("match".equals(json.path("type").asText())) {
                                    String key = json.path("product_id").asText();
                                    kafkaTemplate.send(properties.getTopic(), key, msg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        webSocket.request(1);
                        return null;
                    }
                });
    }
}
