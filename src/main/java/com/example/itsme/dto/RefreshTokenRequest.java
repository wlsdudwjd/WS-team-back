package com.example.itsme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
		@Schema(description = "Refresh token", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank String refreshToken
) {
}
