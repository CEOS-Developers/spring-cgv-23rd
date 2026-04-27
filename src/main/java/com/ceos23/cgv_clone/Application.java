package com.ceos23.cgv_clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableFeignClients(basePackages = "com.ceos23.cgv_clone.payment.client")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
