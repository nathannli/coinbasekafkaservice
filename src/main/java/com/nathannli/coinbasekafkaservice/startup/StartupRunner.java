package com.nathannli.coinbasekafkaservice.startup;

import com.nathannli.coinbasekafkaservice.service.WebSocketService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {
    private final WebSocketService service;

    public StartupRunner(WebSocketService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        service.start();
    }
}

