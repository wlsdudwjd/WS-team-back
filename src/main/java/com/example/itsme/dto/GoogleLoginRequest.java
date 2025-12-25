package com.example.itsme.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleLoginRequest(
		@NotBlank String idToken
) {
}
