package com.example.itsme.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.FieldError;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.example.itsme.dto.ApiErrorResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		Map<String, Object> details = ex.getBindingResult().getFieldErrors().stream()
				.collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage, (a, b) -> b, LinkedHashMap::new));
		return buildResponse(ApiErrorCode.VALIDATION_FAILED, request, details);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
			HttpServletRequest request) {
		Map<String, Object> details = Map.of(ex.getParameterName(), "요청 파라미터가 필요합니다.");
		return buildResponse(ApiErrorCode.INVALID_QUERY_PARAM, request, details);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
			HttpServletRequest request) {
		return buildResponse(ApiErrorCode.DUPLICATE_RESOURCE, request, null);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatus(ResponseStatusException ex,
			HttpServletRequest request) {
		HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
		ApiErrorCode code = mapStatusToErrorCode(status);
		String message = ex.getReason() != null ? ex.getReason() : ex.getMessage();
		return buildResponse(code, request, message, null, ex.getStatusCode().value());
	}

	@ExceptionHandler({ AccessDeniedException.class, AuthorizationDeniedException.class })
	public ResponseEntity<ApiErrorResponse> handleAccessDenied(Exception ex, HttpServletRequest request) {
		return buildResponse(ApiErrorCode.FORBIDDEN, request, ex.getMessage(), null, HttpStatus.FORBIDDEN.value());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		return buildResponse(ApiErrorCode.RESOURCE_NOT_FOUND, request, ex.getMessage(), null,
				HttpStatus.NOT_FOUND.value());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
		return buildResponse(ApiErrorCode.UNKNOWN_ERROR, request, ex.getMessage(), null,
				HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

	private ResponseEntity<ApiErrorResponse> buildResponse(ApiErrorCode code, HttpServletRequest request,
			Map<String, Object> details) {
		return buildResponse(code, request, code.getDefaultMessage(), details, code.getStatus().value());
	}

	private ResponseEntity<ApiErrorResponse> buildResponse(ApiErrorCode code, HttpServletRequest request,
			String message, Map<String, Object> details, int status) {
		ApiErrorResponse body = ApiErrorResponse.of(
				request.getRequestURI(),
				status,
				code.getCode(),
				message,
				details);
		return ResponseEntity.status(status).body(body);
	}

	private ApiErrorCode mapStatusToErrorCode(HttpStatus status) {
		if (status == null) {
			return ApiErrorCode.UNKNOWN_ERROR;
		}
		return switch (status) {
			case BAD_REQUEST -> ApiErrorCode.BAD_REQUEST;
			case UNAUTHORIZED -> ApiErrorCode.UNAUTHORIZED;
			case FORBIDDEN -> ApiErrorCode.FORBIDDEN;
			case NOT_FOUND -> ApiErrorCode.RESOURCE_NOT_FOUND;
			case CONFLICT -> ApiErrorCode.STATE_CONFLICT;
			case UNPROCESSABLE_ENTITY -> ApiErrorCode.UNPROCESSABLE_ENTITY;
			case TOO_MANY_REQUESTS -> ApiErrorCode.TOO_MANY_REQUESTS;
			default -> ApiErrorCode.INTERNAL_SERVER_ERROR;
		};
	}
}
