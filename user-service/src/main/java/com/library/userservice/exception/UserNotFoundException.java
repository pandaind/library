package com.library.userservice.exception;

public class UserNotFoundException extends UserServiceException {
    public UserNotFoundException(String userId) {
        super(ErrorCode.USER_NOT_FOUND, "User not found with ID: " + userId);
    }
    
    public UserNotFoundException(String field, String value) {
        super(ErrorCode.USER_NOT_FOUND, "User not found with " + field + ": " + value);
    }
}