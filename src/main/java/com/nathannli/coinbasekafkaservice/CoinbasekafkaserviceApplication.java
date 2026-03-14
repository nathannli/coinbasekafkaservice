package com.nathannli.coinbasekafkaservice;

import com.nathannli.coinbasekafkaservice.config.CoinbaseProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CoinbaseProperties.class)
public class CoinbasekafkaserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoinbasekafkaserviceApplication.class, args);
	}

}
