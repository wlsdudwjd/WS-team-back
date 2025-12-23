package com.example.itsme.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				// 허용 오리진 확장: 로컬 개발 포트 전반과 배포 도메인까지 받을 수 있도록 패턴 허용
				.allowedOriginPatterns(
						"http://localhost:*",
						"http://127.0.0.1:*",
						"https://*")
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(3600);
	}
}
