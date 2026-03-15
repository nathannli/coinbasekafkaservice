package com.nathannli.coinbasekafkaservice.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic application logger wrapping SLF4J. Use in any class that needs logging
 * by passing the class: {@code new AppLogger(MyClass.class)}.
 */
public class AppLogger {

    private final Logger log;

    public AppLogger(Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    public void info(String format, Object... args) {
        log.info(format, args);
    }

    public void debug(String format, Object... args) {
        log.debug(format, args);
    }

    public void warn(String format, Object... args) {
        log.warn(format, args);
    }

    public void error(String message, Throwable throwable) {
        log.error(message, throwable);
    }
}
