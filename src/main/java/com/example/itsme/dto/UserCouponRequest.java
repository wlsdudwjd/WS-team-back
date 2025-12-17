package com.example.itsme.dto;

import jakarta.validation.constraints.NotNull;

public record UserCouponRequest(
		@NotNull Long userId,
		@NotNull Long couponId,
		@NotNull Boolean isValid
) {
}
