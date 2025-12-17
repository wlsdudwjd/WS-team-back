package com.example.itsme.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MenuCategoryRequest(
		@NotNull Long serviceTypeId,
		@NotBlank String name
) {
}
