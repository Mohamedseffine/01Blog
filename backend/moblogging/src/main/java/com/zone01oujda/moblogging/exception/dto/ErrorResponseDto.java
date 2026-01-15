package com.zone01oujda.moblogging.exception.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;

@Data
public class ErrorResponseDto {
    private Boolean success;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> errors;
    
    public ErrorResponseDto(boolean success, String message, Map<String, String> errors) {
        this.success = success;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}
