package com.example.itsme.dto;

public record MenuLikeStatusResponse(
		Long menuId,
		boolean liked,
		Long totalLikes
) {
}
