package com.example.itsme.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Firebase ID token login payload")
public record FirebaseLoginRequest(
		@Schema(description = "Firebase ID token", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotBlank String idToken
) {
}
