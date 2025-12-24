package com.example.itsme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

public record NotificationRequest(
		Long userId,
		@Email String userEmail,
		@NotBlank String title,
		@NotBlank String body
) {
}
