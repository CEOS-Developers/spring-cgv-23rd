package com.ceos.spring_cgv_23rd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringCgv23rdApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCgv23rdApplication.class, args);
	}

}
