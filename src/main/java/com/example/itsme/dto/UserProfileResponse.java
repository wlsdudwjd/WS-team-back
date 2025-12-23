package com.example.itsme.dto;

import java.time.LocalDateTime;

import com.example.itsme.domain.User;

public record UserProfileResponse(
		Long userId,
		String email,
		String name,
		String phone,
		LocalDateTime createdAt
) {
	public static UserProfileResponse from(User user) {
		return new UserProfileResponse(
				user.getUserId(),
				user.getEmail(),
				user.getName(),
				user.getPhone(),
				user.getCreatedAt()
		);
	}
}
