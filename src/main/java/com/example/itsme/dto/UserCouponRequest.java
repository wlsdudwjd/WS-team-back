package com.example.itsme.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;

public record UserCouponRequest(
		Long userId,
		@Email String userEmail,
		@NotNull Long couponId,
		@NotNull Boolean isValid
) {
}
