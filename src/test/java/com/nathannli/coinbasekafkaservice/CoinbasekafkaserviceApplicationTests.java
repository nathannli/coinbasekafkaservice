package com.nathannli.coinbasekafkaservice;

import com.nathannli.coinbasekafkaservice.config.Properties;
import com.nathannli.coinbasekafkaservice.service.WebSocketProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootTest(properties = {
		"app.coinbase.ws-url=ws://localhost/test",
		"app.coinbase.topic=test-topic",
		"app.coinbase.product-ids[0]=BTC-USD",
		"spring.kafka.bootstrap-servers=localhost:9092"
})
class CoinbasekafkaserviceApplicationTests {

	@Test
	// Verifies the Spring application context loads with test-safe configuration.
	void contextLoads() {
	}

	@TestConfiguration
	static class TestConfig {

		@Bean
		@Primary
		WebSocketProducerService webSocketProducerService() {
			return new WebSocketProducerService(null, new Properties()) {
				@Override
				public void start() {
					// Prevent the ApplicationRunner from opening a real websocket during tests.
				}
			};
		}
	}

}
