package com.example.itsme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
		@Schema(example = "plain-text-password")
		@NotBlank String password,
		@Schema(description = "Login identifier (must be unique email)", example = "user@example.com")
		@NotBlank @Email String email,
		@Schema(example = "홍길동")
		@NotBlank String name,
		@Schema(example = "01012345678")
		String phone
) {
}
