package com.example.itsme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login payload using email as the identifier")
public record LoginRequest(
		@Schema(example = "user@example.com")
		@NotBlank @Email String email,
		@Schema(example = "plain-text-password")
		@NotBlank String password
) {
}
