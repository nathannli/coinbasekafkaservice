package com.nathannli.coinbasekafkaservice.startup;

import com.nathannli.coinbasekafkaservice.config.Properties;
import com.nathannli.coinbasekafkaservice.service.WebSocketProducerService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StartupRunnerTest {

    @Test
    // Verifies application startup triggers the websocket producer service exactly once.
    void runStartsWebSocketProducerService() throws Exception {
        CountingWebSocketProducerService service = new CountingWebSocketProducerService();
        StartupRunner runner = new StartupRunner(service);

        runner.run(null);

        assertEquals(1, service.startCalls);
    }

    private static final class CountingWebSocketProducerService extends WebSocketProducerService {
        private int startCalls;

        private CountingWebSocketProducerService() {
            super(null, new Properties());
        }

        @Override
        public void start() {
            startCalls++;
        }
    }
}
