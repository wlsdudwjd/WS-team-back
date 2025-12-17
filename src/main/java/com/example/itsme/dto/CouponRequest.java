package com.example.itsme.dto;

import java.time.LocalDateTime;

import com.example.itsme.domain.DiscountType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CouponRequest(
		@NotBlank String name,
		@NotNull DiscountType discountType,
		@NotNull Integer discountValue,
		LocalDateTime validFrom,
		LocalDateTime validTo
) {
}
