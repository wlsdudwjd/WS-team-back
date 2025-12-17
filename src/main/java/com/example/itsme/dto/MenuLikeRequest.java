package com.example.itsme.dto;

import jakarta.validation.constraints.NotNull;

public record MenuLikeRequest(
		@NotNull Long userId,
		@NotNull Long menuId
) {
}
