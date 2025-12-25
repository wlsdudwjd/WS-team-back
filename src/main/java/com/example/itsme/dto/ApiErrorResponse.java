package com.example.itsme.dto;

import java.time.OffsetDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
		OffsetDateTime timestamp,
		String path,
		int status,
		String code,
		String message,
		Map<String, Object> details
) {
	public static ApiErrorResponse of(String path, int status, String code, String message,
			Map<String, Object> details) {
		return new ApiErrorResponse(OffsetDateTime.now(), path, status, code, message, details);
	}
}
