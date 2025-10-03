package com.library.userservice.exception;

public class InvalidUserDataException extends UserServiceException {
    public InvalidUserDataException(String message) {
        super(ErrorCode.INVALID_USER_DATA, message);
    }
    
    public InvalidUserDataException(String field, String reason) {
        super(ErrorCode.INVALID_USER_DATA, "Invalid " + field + ": " + reason);
    }
}