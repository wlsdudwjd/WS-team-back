package com.example.itsme.dto;

public record MenuLikeRequest(
		Long userId,
		String userEmail,
		Long menuId
) {
}
