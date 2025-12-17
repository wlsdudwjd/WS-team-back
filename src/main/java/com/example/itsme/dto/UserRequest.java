package com.example.itsme.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
		@NotBlank String password,
		@NotBlank @Email String email,
		@NotBlank String name,
		String phone
) {
}
