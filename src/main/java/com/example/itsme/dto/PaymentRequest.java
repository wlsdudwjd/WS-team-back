package com.example.itsme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
		@NotNull Long userId,
		@NotNull Long orderId,
		@NotBlank String method,
		@NotNull Integer amount
) {
}
