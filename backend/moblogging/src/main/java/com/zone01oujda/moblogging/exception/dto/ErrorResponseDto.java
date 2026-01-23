package com.zone01oujda.moblogging.exception.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for error responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {
    
    /**
     * Success flag (always false for errors)
     */
    private Boolean success;
    
    /**
     * Error message
     */
    private String message;
    
    /**
     * Timestamp when error occurred
     */
    private LocalDateTime timestamp;
    
    /**
     * Field validation errors (optional)
     */
    private Map<String, String> errors;
    
    /**
     * Constructor for creating error response
     * @param success success flag
     * @param message error message
     * @param errors validation errors map
     */
    public ErrorResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}
