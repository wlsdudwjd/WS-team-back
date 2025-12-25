package com.example.itsme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
			if (openApi.getPaths() == null) {
				return;
			}
			openApi.getPaths().forEach((path, pathItem) -> {
				pathItem.readOperations().forEach(operation -> {
					// apply bearer auth to protected APIs
					if (path.startsWith("/api") && !path.startsWith("/api/auth")) {
						operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
						ensureErrorResponses(operation.getResponses());
					}
				});
			});
		};
	}

	private void ensureErrorResponses(io.swagger.v3.oas.models.responses.ApiResponses responses) {
		Map<String, ApiResponse> defaults = Map.of(
				"400", apiError("Bad request / validation failed"),
				"401", apiError("Unauthorized"),
				"403", apiError("Forbidden"),
				"404", apiError("Not found"),
				"422", apiError("Unprocessable entity"),
				"429", apiError("Too many requests"),
				"500", apiError("Internal server error")
		);
		defaults.forEach((code, response) -> responses.computeIfAbsent(code, k -> response));
	}

	private ApiResponse apiError(String description) {
		Schema<?> schema = new Schema<>().$ref("#/components/schemas/" + ApiErrorResponse.class.getSimpleName());
		Content content = new Content().addMediaType("application/json", new MediaType().schema(schema));
		return new ApiResponse().description(description).content(content);
	}
}
