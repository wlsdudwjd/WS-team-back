package com.example.itsme.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemCreateRequest(
		@NotNull Long orderId,
		@NotNull Long menuId,
		@Min(1) int quantity,
		@NotNull Integer unitPrice
) {
}
