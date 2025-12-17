package com.example.itsme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuRequest(
		@NotNull Long storeId,
		@NotNull Long categoryId,
		@NotBlank String name,
		@NotNull Integer price,
		String description
) {
}
