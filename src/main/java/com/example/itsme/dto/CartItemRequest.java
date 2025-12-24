package com.example.itsme.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

public record CartItemRequest(
		@NotNull Long cartId,
		Long userId,
		@Email String userEmail,
		@NotNull Long menuId,
		@Min(1) int quantity
) {
}
