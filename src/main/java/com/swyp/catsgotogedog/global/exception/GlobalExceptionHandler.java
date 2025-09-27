package com.swyp.catsgotogedog.global.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;

import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CatsgotogedogException.class)
	protected ResponseEntity<CatsgotogedogApiResponse<Object>> handleCatsgotogedogException(CatsgotogedogException ex) {
		log.error("CatsgotogedogException : {}", ex.getMessage(), ex);
		return createErrorResponse(ex.getErrorCode());
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	protected ResponseEntity<CatsgotogedogApiResponse<Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
		log.error("CatsgotogedogException: {}", e.getMessage(), e);
		int errorCode = ErrorCode.IMAGE_SIZE_EXCEEDED.getCode();
		CatsgotogedogApiResponse<Object> response = CatsgotogedogApiResponse.fail(ErrorCode.IMAGE_SIZE_EXCEEDED);
		return ResponseEntity
				.status(errorCode)
				.body(response);
	}

	@ExceptionHandler(Exception.class)
	protected ResponseEntity<CatsgotogedogApiResponse<Object>> handleException(Exception ex) {
		log.error("Exception : {}", ex.getMessage(), ex);
		int errorCode = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
		CatsgotogedogApiResponse<Object> response = CatsgotogedogApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		return ResponseEntity
			.status(errorCode)
			.body(response);
	}

	private ResponseEntity<CatsgotogedogApiResponse<Object>> createErrorResponse(ErrorCode errorCode) {
		int errorCodeValue = errorCode.getCode();
		CatsgotogedogApiResponse<Object> response = CatsgotogedogApiResponse.fail(errorCode);
		return ResponseEntity
			.status(errorCodeValue)
			.body(response);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	protected ResponseEntity<CatsgotogedogApiResponse<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		log.error("MethodArgumentTypeMismatchException: {}", e.getMessage(), e);
		String errorMessage = String.format("파라미터 '%s'의 타입이 올바르지 않습니다. 요청된 타입: %s",
			e.getName(), e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "알 수 없음");
		CatsgotogedogApiResponse<Object> response = CatsgotogedogApiResponse.fail(HttpStatus.BAD_REQUEST.value(), errorMessage);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<CatsgotogedogApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		log.error("MethodArgumentNotValidException: {}", e.getMessage(), e);
		BindingResult bindingResult = e.getBindingResult();
		Map<String, String> errors = new HashMap<>();
		bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

		CatsgotogedogApiResponse<Object> response = CatsgotogedogApiResponse.fail(ErrorCode.ARGUMENT_NOT_VALID, errors);

		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(response);
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	protected ResponseEntity<CatsgotogedogApiResponse<Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
		log.error("MissingServletRequestParameterException: {}", e.getMessage(), e);
		CatsgotogedogApiResponse<Object> response = CatsgotogedogApiResponse.fail(ErrorCode.MISSING_REQUEST_PARAMETER, e.getParameterName());
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(response);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	protected ResponseEntity<CatsgotogedogApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		log.error("HttpMessageNotReadableException: {}", e.getMessage(), e);
		CatsgotogedogApiResponse<Object> response = CatsgotogedogApiResponse.fail(ErrorCode.MESSAGE_NOT_READABLE);
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(response);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	protected ResponseEntity<CatsgotogedogApiResponse<Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException  e) {
		log.error("HttpRequestMethodNotSupportedException: {}", e.getMessage(), e);
		CatsgotogedogApiResponse<Object> response = CatsgotogedogApiResponse.fail(ErrorCode.METHOD_NOT_ALLOWED, e.getMethod());
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(response);
	}

	@ExceptionHandler(UnAuthorizedAccessException.class)
	protected ResponseEntity<CatsgotogedogApiResponse<Object>> handleUnAuthorizedAccessException(UnAuthorizedAccessException  e) {
		log.error("UnAuthorizedAccessException: {}", e.getMessage(), e);
		CatsgotogedogApiResponse<Object> response = CatsgotogedogApiResponse.fail(ErrorCode.UNAUTHORIZED_ACCESS);
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(response);
	}

	@ExceptionHandler(ExpiredTokenException.class)
	protected ResponseEntity<CatsgotogedogApiResponse<Object>> handleExpiredTokenException(ExpiredTokenException e) {
		log.warn("ExpiredTokenException : {}", e.getMessage(), e);
		CatsgotogedogApiResponse<Object> response = CatsgotogedogApiResponse.fail(ErrorCode.EXPIRED_TOKEN);
		return ResponseEntity
			.status(HttpStatus.UNAUTHORIZED)
			.body(response);
	}

	@ExceptionHandler(MalformedJwtException.class)
	protected ResponseEntity<CatsgotogedogApiResponse<Object>> handleMalformedJwtException(MalformedJwtException e) {
		CatsgotogedogApiResponse<Object> response = CatsgotogedogApiResponse.fail(HttpStatus.BAD_REQUEST.value() , e.getMessage());
		return ResponseEntity
			.status(HttpStatus.BAD_REQUEST)
			.body(response);
	}

	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	protected ResponseEntity<CatsgotogedogApiResponse<Object>> handleHttpMediaTypeNotSupportedException(MalformedJwtException e) {
		CatsgotogedogApiResponse<Object> response = CatsgotogedogApiResponse.fail(ErrorCode.MEDIA_TYPE_NOT_SUPPORTED , ErrorCode.MEDIA_TYPE_NOT_SUPPORTED.getMessage());
		return ResponseEntity
			.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
			.body(response);
	}

}
