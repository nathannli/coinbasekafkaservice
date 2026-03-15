package com.nathannli.coinbasekafkaservice.service;

import com.nathannli.coinbasekafkaservice.config.Properties;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.mock.MockProducerFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WebSocketProducerServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private MockProducer<String, String> mockProducer;
    private Properties properties;
    private WebSocketProducerService service;

    @BeforeEach
    void setUp() {
        mockProducer = new MockProducer<String, String>(true, null, new StringSerializer(), new StringSerializer());
        KafkaTemplate<String, String> kafkaTemplate =
                new KafkaTemplate<>(new MockProducerFactory<>(() -> mockProducer));

        properties = new Properties();
        properties.setWsUrl("wss://ws-feed.exchange.coinbase.com");
        properties.setTopic("coinbase-trades");
        properties.setProductIds(List.of("BTC-USD"));
        service = new WebSocketProducerService(kafkaTemplate, properties);
    }

    @Test
    // Verifies the websocket subscribe payload contains the configured products and matches channel.
    void buildSubscribeMessageIncludesConfiguredProductsAndChannel() throws Exception {
        String subscribeMessage = service.buildSubscribeMessage();

        JsonNode json = objectMapper.readTree(subscribeMessage);

        assertEquals("subscribe", json.path("type").asText());
        assertEquals("matches", json.path("channels").get(0).asText());
        assertEquals("BTC-USD", json.path("product_ids").get(0).asText());
    }

    @Test
    // Verifies Coinbase match events are forwarded to Kafka with the expected topic, key, and value.
    void handleMessageSendsMatchEventsToKafka() {
        String message = "{\"type\":\"match\",\"product_id\":\"BTC-USD\",\"price\":\"100.00\"}";

        service.handleMessage(message);

        assertEquals(1, mockProducer.history().size());
        ProducerRecord<String, String> record = mockProducer.history().get(0);
        assertEquals("coinbase-trades", record.topic());
        assertEquals("BTC-USD", record.key());
        assertEquals(message, record.value());
    }

    @Test
    // Verifies non-match websocket events are ignored and do not produce Kafka records.
    void handleMessageIgnoresNonMatchEvents() {
        service.handleMessage("{\"type\":\"subscriptions\"}");

        assertTrue(mockProducer.history().isEmpty());
    }

    @Test
    // Verifies malformed websocket payloads are ignored without publishing to Kafka.
    void handleMessageIgnoresMalformedJson() {
        service.handleMessage("{bad-json");

        assertTrue(mockProducer.history().isEmpty());
    }

    @Test
    // Verifies fragmented websocket text frames are reassembled before Kafka publishing.
    void listenerReassemblesFragmentedMessagesBeforePublishing() {
        RecordingWebSocket webSocket = new RecordingWebSocket();
        WebSocket.Listener listener = service.createListener(properties.getWsUrl());
        String partOne = "{\"type\":\"match\",";
        String partTwo = "\"product_id\":\"BTC-USD\"}";

        listener.onText(webSocket, partOne, false);
        listener.onText(webSocket, partTwo, true);

        assertEquals(1, mockProducer.history().size());
        assertEquals(partOne + partTwo, mockProducer.history().get(0).value());
        assertEquals(2, webSocket.requestCalls.size());
        assertEquals(1L, webSocket.requestCalls.get(0));
        assertEquals(1L, webSocket.requestCalls.get(1));
    }

    @Test
    // Verifies websocket open sends the Coinbase subscribe message and requests the first frame.
    void listenerSendsSubscribeMessageOnOpen() throws Exception {
        RecordingWebSocket webSocket = new RecordingWebSocket();
        WebSocket.Listener listener = service.createListener(properties.getWsUrl());

        listener.onOpen(webSocket);

        assertEquals(List.of(service.buildSubscribeMessage()), webSocket.sentTexts);
        assertEquals(List.of(1L), webSocket.requestCalls);
    }

    private static final class RecordingWebSocket implements WebSocket {
        private final List<String> sentTexts = new ArrayList<>();
        private final List<Long> requestCalls = new ArrayList<>();

        @Override
        public CompletableFuture<WebSocket> sendText(CharSequence data, boolean last) {
            sentTexts.add(data.toString());
            return CompletableFuture.completedFuture(this);
        }

        @Override
        public CompletableFuture<WebSocket> sendBinary(ByteBuffer data, boolean last) {
            return CompletableFuture.completedFuture(this);
        }

        @Override
        public CompletableFuture<WebSocket> sendPing(ByteBuffer message) {
            return CompletableFuture.completedFuture(this);
        }

        @Override
        public CompletableFuture<WebSocket> sendPong(ByteBuffer message) {
            return CompletableFuture.completedFuture(this);
        }

        @Override
        public CompletableFuture<WebSocket> sendClose(int statusCode, String reason) {
            return CompletableFuture.completedFuture(this);
        }

        @Override
        public void request(long n) {
            requestCalls.add(n);
        }

        @Override
        public String getSubprotocol() {
            return null;
        }

        @Override
        public boolean isOutputClosed() {
            return false;
        }

        @Override
        public boolean isInputClosed() {
            return false;
        }

        @Override
        public void abort() {
        }
    }
}
