package com.library.userservice.exception;

public enum ErrorCode {
    USER_NOT_FOUND("USER_001", "User not found"),
    USER_ALREADY_EXISTS("USER_002", "User already exists"),
    EMAIL_ALREADY_EXISTS("USER_003", "Email address already exists"),
    USERNAME_ALREADY_EXISTS("USER_004", "Username already exists"),
    INVALID_USER_DATA("USER_005", "Invalid user data provided"),
    USER_NOT_ACTIVE("USER_006", "User account is not active"),
    USER_SUSPENDED("USER_007", "User account is suspended"),
    BORROWING_LIMIT_EXCEEDED("USER_008", "User has exceeded borrowing limit"),
    OUTSTANDING_FINES("USER_009", "User has outstanding fines"),
    INVALID_CREDENTIALS("USER_010", "Invalid login credentials"),
    DATABASE_ERROR("USER_011", "Database operation failed"),
    VALIDATION_ERROR("USER_012", "Input validation failed"),
    INTERNAL_ERROR("USER_013", "Internal server error");
    
    private final String code;
    private final String message;
    
    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}