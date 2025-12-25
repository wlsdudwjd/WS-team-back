package com.example.itsme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.example.itsme.domain.Role;

public record UserRequest(
		@Schema(example = "plain-text-password")
		@NotBlank String password,
		@Schema(description = "User-chosen login id (must be unique)", example = "id")
		@NotBlank String username,
		@Schema(description = "Login identifier (must be unique email)", example = "user@example.com")
		@NotBlank @Email String email,
		@Schema(example = "홍길동")
		@NotBlank String name,
		@Schema(example = "01012345678")
		@NotBlank String phone,
		@Schema(example = "USER", description = "Role for the user (USER or ADMIN)")
		Role role
) {
}
