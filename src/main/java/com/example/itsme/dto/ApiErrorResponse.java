package com.example.itsme.dto;

import java.time.OffsetDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiErrorResponse", description = "공통 에러 응답")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
		@Schema(example = "2025-03-05T12:34:56Z") OffsetDateTime timestamp,
		@Schema(example = "/api/posts/1") String path,
		@Schema(example = "400") int status,
		@Schema(example = "VALIDATION_FAILED") String code,
		@Schema(example = "필드 유효성 검사 실패") String message,
		@Schema(description = "추가 정보", example = "{\"title\": \"현재 길이 150자\"}")
		Map<String, Object> details
) {
	public static ApiErrorResponse of(String path, int status, String code, String message,
			Map<String, Object> details) {
		return new ApiErrorResponse(OffsetDateTime.now(), path, status, code, message, details);
	}
}
