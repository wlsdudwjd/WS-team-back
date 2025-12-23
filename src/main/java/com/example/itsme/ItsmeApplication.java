package com.example.itsme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Itsme API", version = "v1", description = "Backend APIs with email-based login identifier"))
public class ItsmeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItsmeApplication.class, args);
	}

}
