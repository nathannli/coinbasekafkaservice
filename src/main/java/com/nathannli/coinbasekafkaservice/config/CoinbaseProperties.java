package com.nathannli.coinbasekafkaservice.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.coinbase")
public class CoinbaseProperties {

    private String wsUrl = "wss://ws-feed.exchange.coinbase.com";
    private String topic = "coinbase-trades";
    private List<String> productIds = List.of("BTC-USD");

    public String getWsUrl() {
        return wsUrl;
    }

    public void setWsUrl(String wsUrl) {
        this.wsUrl = wsUrl;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<String> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<String> productIds) {
        this.productIds = productIds;
    }
}
