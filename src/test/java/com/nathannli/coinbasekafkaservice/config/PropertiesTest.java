package com.nathannli.coinbasekafkaservice.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertiesTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    // Verifies validation succeeds when all required Coinbase properties are provided.
    void validationPassesWithAllRequiredValues() {
        Properties properties = new Properties();
        properties.setWsUrl("wss://ws-feed.exchange.coinbase.com");
        properties.setTopic("coinbase-trades");
        properties.setProductIds(List.of("BTC-USD"));

        Set<ConstraintViolation<Properties>> violations = validator.validate(properties);

        assertTrue(violations.isEmpty());
    }

    @Test
    // Verifies validation fails when mandatory Coinbase properties are missing.
    void validationFailsWhenRequiredValuesAreMissing() {
        Properties properties = new Properties();

        Set<ConstraintViolation<Properties>> violations = validator.validate(properties);

        assertEquals(3, violations.size());
    }
}
