# Coinbase Kafka Service

A Spring Boot service that subscribes to the [Coinbase Advanced Trade WebSocket feed](https://docs.cdp.coinbase.com/coinbase-app/advanced-trade-apis/websocket/websocket-overview) and publishes trade match events to a Kafka topic.

## Requirements

- Java 17+
- Maven
- Spring Boot 4.0.3
- Apache Kafka (standalone KRaft mode supported via included config)

## How to Use

### 1. Start Kafka

From your Kafka installation directory, run the included initializer script:

```sh
fish /path/to/coinbasekafkaservice/bin/initializer.fish
```

This formats storage and starts a standalone KRaft broker on `localhost:9092`.

### 2. Configure the Application

Set the following properties in `src/main/resources/application.properties`:

```properties
app.coinbase.ws-url=wss://advanced-trade-ws.coinbase.com
app.coinbase.topic=coinbase-matches
app.coinbase.product-ids=BTC-USD,ETH-USD

spring.kafka.bootstrap-servers=localhost:9092
```

All three `app.coinbase.*` properties are required — the app will fail to start if any are missing or blank.

### 3. Run the Service

```sh
./mvnw spring-boot:run
```

The service will connect to the Coinbase WebSocket feed on startup and begin publishing `match` events to the configured Kafka topic, keyed by `product_id`.
