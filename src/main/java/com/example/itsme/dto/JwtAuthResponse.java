package com.example.itsme.dto;

import com.example.itsme.domain.Role;

public record JwtAuthResponse(
		Long userId,
		String username,
		String email,
		String name,
		Role role,
		String accessToken,
		String tokenType,
		String refreshToken
) {
	public static JwtAuthResponse of(Long userId, String username, String email, String name, Role role,
			String accessToken, String refreshToken) {
		return new JwtAuthResponse(userId, username, email, name, role, accessToken, "Bearer", refreshToken);
	}
}
