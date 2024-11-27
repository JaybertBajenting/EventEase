package com.capstone.EventEase;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@OpenAPIDefinition

public class EventEaseApplication {


	public static void main(String[] args) {
		SpringApplication.run(EventEaseApplication.class, args);

	}




}



