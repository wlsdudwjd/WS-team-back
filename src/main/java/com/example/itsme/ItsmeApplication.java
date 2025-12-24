package com.example.itsme;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Itsme API",
				version = "v1",
				description = "Campus ordering backend: email/Firebase 로그인, 메뉴·주문·쿠폰·결제 관리"),
		servers = {
				@Server(url = "http://localhost:8080", description = "Local"),
				@Server(url = "http://113.198.66.68:10086", description = "Public")
		}
)
public class ItsmeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItsmeApplication.class, args);
	}

}
