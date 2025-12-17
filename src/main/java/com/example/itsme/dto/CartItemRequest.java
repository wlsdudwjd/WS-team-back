package com.example.itsme.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemRequest(
		@NotNull Long cartId,
		@NotNull Long menuId,
		@Min(1) int quantity
) {
}
