package com.example.itsme.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ApiErrorCode {
	BAD_REQUEST("BAD_REQUEST", HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않음"),
	VALIDATION_FAILED("VALIDATION_FAILED", HttpStatus.BAD_REQUEST, "필드 유효성 검사 실패"),
	INVALID_QUERY_PARAM("INVALID_QUERY_PARAM", HttpStatus.BAD_REQUEST, "쿼리 파라미터 값이 잘못됨"),

	UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "인증 토큰 없음 또는 잘못된 토큰"),
	TOKEN_EXPIRED("TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED, "토큰 만료"),

	FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN, "접근 권한 없음"),

	RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", HttpStatus.NOT_FOUND, "요청한 리소스가 존재하지 않음"),
	USER_NOT_FOUND("USER_NOT_FOUND", HttpStatus.NOT_FOUND, "사용자 ID가 존재하지 않음"),

	DUPLICATE_RESOURCE("DUPLICATE_RESOURCE", HttpStatus.CONFLICT, "중복 데이터 존재"),
	STATE_CONFLICT("STATE_CONFLICT", HttpStatus.CONFLICT, "리소스 상태 충돌"),

	UNPROCESSABLE_ENTITY("UNPROCESSABLE_ENTITY", HttpStatus.UNPROCESSABLE_ENTITY, "처리할 수 없는 요청 내용"),
	TOO_MANY_REQUESTS("TOO_MANY_REQUESTS", HttpStatus.TOO_MANY_REQUESTS, "요청 한도 초과"),

	DATABASE_ERROR("DATABASE_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "DB 연동 오류"),
	INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류"),
	UNKNOWN_ERROR("UNKNOWN_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류");

	private final String code;
	private final HttpStatus status;
	private final String defaultMessage;

	ApiErrorCode(String code, HttpStatus status, String defaultMessage) {
		this.code = code;
		this.status = status;
		this.defaultMessage = defaultMessage;
	}
}
