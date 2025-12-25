package com.example.itsme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OpenApiCustomizer;

import com.example.itsme.dto.ApiErrorResponse;

import java.util.Map;

@Configuration
@SecurityScheme(
		name = "bearerAuth",
		type = SecuritySchemeType.HTTP,
		scheme = "bearer",
		bearerFormat = "JWT",
		in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {

	@Bean
	public OpenApiCustomizer securityAndErrorResponsesCustomizer() {
		return openApi -> {
			// 보장: ApiErrorResponse 스키마를 components에 등록
			Components components = openApi.getComponents();
			if (components == null) {
				components = new Components();
				openApi.setComponents(components);
			}
			ModelConverters.getInstance().read(ApiErrorResponse.class)
					.forEach(components::addSchemas);

			if (openApi.getPaths() == null) {
				return;
			}
			openApi.getPaths().forEach((path, pathItem) -> {
				pathItem.readOperations().forEach(operation -> {
					// apply bearer auth to protected APIs
					if (path.startsWith("/api") && !path.startsWith("/api/auth")) {
						operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
					}
					// ensure error response schema is attached (Swagger 기본 "string" 응답 덮어쓰기)
					ensureErrorResponses(operation.getResponses());
				});
			});
		};
	}

	private void ensureErrorResponses(io.swagger.v3.oas.models.responses.ApiResponses responses) {
		Map<String, ApiResponse> defaults = Map.of(
				"400", apiError("Bad request / validation failed", "VALIDATION_FAILED", 400, "필드 유효성 검사 실패", "/api/example/validation"),
				"401", apiError("Unauthorized", "UNAUTHORIZED", 401, "인증 토큰이 없거나 잘못되었습니다", "/api/example/auth"),
				"403", apiError("Forbidden", "FORBIDDEN", 403, "접근 권한이 없습니다", "/api/example/forbidden"),
				"404", apiError("Not found", "RESOURCE_NOT_FOUND", 404, "요청한 리소스를 찾을 수 없습니다", "/api/example/not-found"),
				"422", apiError("Unprocessable entity", "UNPROCESSABLE_ENTITY", 422, "요청을 처리할 수 없습니다", "/api/example/unprocessable"),
				"429", apiError("Too many requests", "TOO_MANY_REQUESTS", 429, "요청 한도를 초과했습니다", "/api/example/rate-limit"),
				"500", apiError("Internal server error", "INTERNAL_SERVER_ERROR", 500, "서버 내부 오류", "/api/example/error")
		);
		defaults.forEach(responses::put);
	}

	private ApiResponse apiError(String description, String code, int status, String message, String path) {
		Schema<?> schema = new Schema<>().$ref("#/components/schemas/" + ApiErrorResponse.class.getSimpleName());
		Map<String, Object> example = new java.util.LinkedHashMap<>();
		example.put("timestamp", "2025-03-05T12:34:56Z");
		example.put("path", path);
		example.put("status", status);
		example.put("code", code);
		example.put("message", message);
		example.put("details", Map.of("hint", "추가 정보"));
		Content content = new Content().addMediaType("application/json",
				new MediaType().schema(schema).example(example));
		return new ApiResponse().description(description).content(content);
	}
}
