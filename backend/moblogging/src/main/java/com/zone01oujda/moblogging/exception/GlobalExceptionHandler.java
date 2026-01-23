package com.zone01oujda.moblogging.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import com.zone01oujda.moblogging.exception.dto.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {
        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return ResponseEntity.badRequest().body(new ErrorResponseDto(false, "Validation failed"));
        }

        @ExceptionHandler(org.springframework.web.multipart.MultipartException.class)
        public ResponseEntity<ErrorResponseDto> handleMultipart(MultipartException ex) {
                // log root cause
                Throwable root = ex;
                while (root.getCause() != null)
                        root = root.getCause();

                logger.error("Multipart error: {} {}", root.getClass().getName(), root.getMessage(), ex);

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

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponseDto> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex) {

                String message = "Database constraint violation";

                if (ex.getMostSpecificCause().getMessage().contains("email")) {
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
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ErrorResponseDto(false, "An unexpected error occurred"));
        }

}
