package com.zone01oujda.moblogging.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.zone01oujda.moblogging.exception.dto.ErrorResponseDto;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return ResponseEntity.badRequest().body(new ErrorResponseDto(false, "Validation failed", errors));
        }

        @ExceptionHandler(org.springframework.web.multipart.MultipartException.class)
        public ResponseEntity<ErrorResponseDto> handleMultipart(MultipartException ex) {
                // log root cause
                Throwable root = ex;
                while (root.getCause() != null)
                        root = root.getCause();

                logger.warn("Multipart error: {} {}", root.getClass().getName(), root.getMessage());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(new ErrorResponseDto(false,
                                                "Multipart error: " + root.getClass().getSimpleName() + ": "
                                                                + root.getMessage()));
        }

        @SuppressWarnings("deprecation")
        @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
        public ResponseEntity<ErrorResponseDto> handleMax(MaxUploadSizeExceededException ex) {
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                                .body(new ErrorResponseDto(false, "Upload too large"));
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponseDto> handleNotFound(ResourceNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(false, ex.getMessage()));
        }

        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ErrorResponseDto> handleConflict(ConflictException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(false, ex.getMessage()));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponseDto> handleForbidden(AccessDeniedException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponseDto(false, ex.getMessage()));
        }

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ErrorResponseDto> handleBadRequest(BadRequestException ex) {
                return ResponseEntity.badRequest().body(new ErrorResponseDto(false, ex.getMessage()));
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponseDto> handleConstraintViolation(ConstraintViolationException ex) {
                Map<String, String> errors = new HashMap<>();

                ex.getConstraintViolations().forEach(violation -> {
                        String path = String.valueOf(violation.getPropertyPath());
                        errors.put(path, violation.getMessage());
                });

                return ResponseEntity.badRequest().body(new ErrorResponseDto(false, "Validation failed", errors));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ErrorResponseDto> handleUnreadableMessage(HttpMessageNotReadableException ex) {
                return ResponseEntity.badRequest()
                                .body(new ErrorResponseDto(false, "Malformed JSON request"));
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ErrorResponseDto> handleArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
                String name = ex.getName();
                String message = (name == null || name.isBlank())
                                ? "Invalid request parameter"
                                : "Invalid value for parameter: " + name;
                return ResponseEntity.badRequest().body(new ErrorResponseDto(false, message));
        }

        @ExceptionHandler(NoHandlerFoundException.class)
        public ResponseEntity<ErrorResponseDto> handleNoHandlerFound(NoHandlerFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponseDto(false, "Resource not found"));
        }

        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<ErrorResponseDto> handleNoResourceFound(NoResourceFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ErrorResponseDto(false, "Resource not found"));
        }

        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ErrorResponseDto> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                                .body(new ErrorResponseDto(false, "Method not allowed"));
        }

        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponseDto> handleAuthentication(AuthenticationException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(new ErrorResponseDto(false, "Authentication required"));
        }

        @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
        public ResponseEntity<ErrorResponseDto> handleSecurityAccessDenied(
                        org.springframework.security.access.AccessDeniedException ex) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(new ErrorResponseDto(false, "Access is denied"));
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex) {

                String message = "Database constraint violation";

                if (ex.getMostSpecificCause() != null
                                && ex.getMostSpecificCause().getMessage() != null
                                && ex.getMostSpecificCause().getMessage().contains("email")) {
                        message = "Email already exists";
                }

                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDto(false, message));
        }

        @ExceptionHandler(DataAccessException.class)
        public ResponseEntity<ErrorResponseDto> handleDatabaseError(
                        DataAccessException ex) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(
                                false,
                                "Database error occurred"));
        }

        @ExceptionHandler(EmptyResultDataAccessException.class)
        public ResponseEntity<ErrorResponseDto> handleEmptyResult(
                        EmptyResultDataAccessException ex) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(
                                false,
                                "Resource not found"));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
                logger.error("Unhandled exception", ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponseDto(false, "An unexpected error occurred"));
        }

}
