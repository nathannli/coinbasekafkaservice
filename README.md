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

Configuration is split by profile. Edit `application-dev.properties` or `application-prod.properties` under `src/main/resources/` as needed. Required properties:

- `app.coinbase.ws-url` — Coinbase WebSocket URL
- `app.coinbase.topic` — Kafka topic for match events
- `app.coinbase.product-ids` — Comma-separated product ids (e.g. `BTC-USD,ETH-USD`)
- `spring.kafka.bootstrap-servers` — Kafka broker address(es)

All three `app.coinbase.*` properties are required; the app will fail to start if any are missing or blank.

### 3. Run the Service

**Dev (Maven, `dev` profile):**

```sh
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Prod (packaged JAR, `prod` profile):**

```sh
java -jar target/coinbasekafkaservice-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

Build the JAR first with `./mvnw package` if needed. The service connects to the Coinbase WebSocket on startup and publishes `match` events to the configured Kafka topic, keyed by `product_id`.
