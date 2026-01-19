package com.zone01oujda.moblogging.util.response;

public class ApiResponseBuilder<T> {
    
    private boolean success;
    private String message;
    private T data;
    private Object errors;
    
    public ApiResponseBuilder<T> success(boolean success) {
        this.success = success;
        return this;
    }
    
    public ApiResponseBuilder<T> message(String message) {
        this.message = message;
        return this;
    }
    
    public ApiResponseBuilder<T> data(T data) {
        this.data = data;
        return this;
    }
    
    public ApiResponseBuilder<T> errors(Object errors) {
        this.errors = errors;
        return this;
    }
    
    public ApiResponse<T> build() {
        return new ApiResponse<>(success, message, data, errors);
    }
    
    public static <T> ApiResponseBuilder<T> builder() {
        return new ApiResponseBuilder<>();
    }
}
