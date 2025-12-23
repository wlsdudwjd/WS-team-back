package com.example.itsme.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login success response")
public record AuthResponse(
		@Schema(description = "Internal user id", example = "1")
		Long userId,
		@Schema(description = "Login email", example = "user@example.com")
		String email,
		@Schema(description = "Display name", example = "홍길동")
		String name
) {
}
