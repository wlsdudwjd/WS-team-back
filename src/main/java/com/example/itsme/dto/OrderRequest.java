package com.example.itsme.dto;

import java.util.List;

import com.example.itsme.domain.OrderStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
		@NotNull Long userId,
		@NotNull Long storeId,
		@NotNull OrderStatus status,
		@NotNull Integer totalPrice,
		@NotEmpty List<@Valid OrderItemRequest> items
) {
}
