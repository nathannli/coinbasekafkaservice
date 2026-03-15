package com.nathannli.coinbasekafkaservice.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.stereotype.Component;

/**
 * Configuration for the Coinbase WebSocket feed. All properties are mandatory and
 * must be set in {@code application.properties} under the {@code app.coinbase} prefix:
 * <ul>
 *   <li>{@code app.coinbase.ws-url}</li>
 *   <li>{@code app.coinbase.topic}</li>
 *   <li>{@code app.coinbase.product-ids}</li>
 * </ul>
 * Missing or blank values will cause application startup to fail.
 */
@Validated
@Component
@ConfigurationProperties(prefix = "app.coinbase")
public class Properties {

    @NotBlank(message = "app.coinbase.ws-url is mandatory")
    private String wsUrl;

    @NotBlank(message = "app.coinbase.topic is mandatory")
    private String topic;

    @NotEmpty(message = "app.coinbase.product-ids is mandatory and must contain at least one product id")
    private List<String> productIds;

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
