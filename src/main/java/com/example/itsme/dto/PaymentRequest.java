package com.example.itsme.dto;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
		@NotNull Long userId,
		@NotNull Long orderId,
		@NotNull Integer method,
		@NotNull Integer amount
) {
}
