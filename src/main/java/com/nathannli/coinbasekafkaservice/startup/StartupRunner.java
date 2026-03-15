package com.nathannli.coinbasekafkaservice.startup;

import com.nathannli.coinbasekafkaservice.service.WebSocketProducerService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {
    private final WebSocketProducerService service;

    public StartupRunner(WebSocketProducerService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        service.start();
    }
}
