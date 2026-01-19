package com.zone01oujda.moblogging.util.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Generic API response wrapper
 * @param <T> the type of the response data
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    /**
     * Whether the request was successful
     */
    private boolean success;
    
    /**
     * Response message
     */
    private String message;
    
    /**
     * Response data
     */
    private T data;
    
    /**
     * Response timestamp
     */
    private LocalDateTime timestamp;
    
    /**
     * Error details (if applicable)
     */
    private Object errors;
    
    /**
     * Constructor without timestamp (auto-generated)
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * Constructor with success status and message
     */
    public ApiResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }
    
    /**
     * Constructor with success status, message, and data
     */
    public ApiResponse(boolean success, String message, T data) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    /**
     * Constructor with all fields except errors
     */
    public ApiResponse(boolean success, String message, T data, Object errors) {
        this();
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }
}
