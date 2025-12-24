package com.example.itsme.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

public record CartRequest(
		Long userId,
		@Email String userEmail
) {
}
