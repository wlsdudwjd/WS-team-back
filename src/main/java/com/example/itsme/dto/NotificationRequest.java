package com.example.itsme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationRequest(
		@NotNull Long userId,
		@NotBlank String title,
		@NotBlank String body
) {
}
