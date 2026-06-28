package com.shivani.scribeapi.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.shivani.scribeapi.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// Handles entity resource missing errors (e.g., User or Post not found)
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
	}

	// Wrap raw parameter maps into structured data envelopes
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleMethodArgsNotValidException(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();

		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String message = error.getDefaultMessage();
			errors.put(fieldName, message);
		});

		// Explicitly format timestamp to a reliable ISO standard string to guarantee
		// cross-platform Jackson serialization without library dependencies
		String formattedTimestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

		// Structured API response layout
		Map<String, Object> errorResponseEnvelope = new HashMap<>();
		errorResponseEnvelope.put("timestamp", formattedTimestamp);
		errorResponseEnvelope.put("success", false);
		errorResponseEnvelope.put("status", HttpStatus.BAD_REQUEST.value());
		errorResponseEnvelope.put("validationErrors", errors);

		return new ResponseEntity<>(errorResponseEnvelope, HttpStatus.BAD_REQUEST);
	}

	// Handles authentication and validation issues
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiResponse> handleApiException(ApiException ex) {
		String message = ex.getMessage();
		ApiResponse apiResponse = new ApiResponse(message, false);
		return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
	}
}
