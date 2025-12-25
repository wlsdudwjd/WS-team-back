package com.example.itsme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import jakarta.servlet.http.HttpServletResponse;

import com.example.itsme.dto.ApiErrorResponse;
import com.example.itsme.exception.ApiErrorCode;
import com.example.itsme.security.JwtAuthenticationFilter;
import com.example.itsme.security.RateLimitFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final RateLimitFilter rateLimitFilter;
	private final ObjectMapper objectMapper;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.cors(Customizer.withDefaults())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(authenticationEntryPoint())
						.accessDeniedHandler(accessDeniedHandler()))
				.authorizeHttpRequests(auth -> auth
						// 공개 엔드포인트
						.requestMatchers(
								"/api/auth/**",
								"/swagger-ui/**",
								"/v3/api-docs/**",
								"/swagger-resources/**",
								"/actuator/**",
								"/health",
								"/error")
						.permitAll()
						// 정적 리소스 & SPA 루트는 토큰 없이 접근 허용
						.requestMatchers(HttpMethod.GET,
								"/",
								"/index.html",
								"/assets/**",
								"/favicon.ico",
								"/**/*.css",
								"/**/*.js",
								"/**/*.png",
								"/**/*.jpg",
								"/**/*.jpeg",
								"/**/*.gif",
								"/**/*.svg",
								"/**/*.ico")
						.permitAll()
						// 나머지 API는 인증 필요
						.requestMatchers("/api/**").authenticated()
						// 기타는 SPA forward를 위해 허용
				.anyRequest().permitAll())
				.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	private AuthenticationEntryPoint authenticationEntryPoint() {
		return (request, response, authException) -> writeError(response, request.getRequestURI(),
				ApiErrorCode.UNAUTHORIZED);
	}

	private AccessDeniedHandler accessDeniedHandler() {
		return (request, response, accessDeniedException) -> writeError(response, request.getRequestURI(),
				ApiErrorCode.FORBIDDEN);
	}

	private void writeError(HttpServletResponse response, String path, ApiErrorCode code)
			throws java.io.IOException {
		ApiErrorResponse body = ApiErrorResponse.of(path, code.getStatus().value(), code.getCode(),
				code.getDefaultMessage(), null);
		response.setStatus(code.getStatus().value());
		response.setContentType("application/json");
		objectMapper.writeValue(response.getOutputStream(), body);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
