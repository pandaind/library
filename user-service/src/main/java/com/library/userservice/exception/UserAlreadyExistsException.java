package com.library.userservice.exception;

public class UserAlreadyExistsException extends UserServiceException {
    public UserAlreadyExistsException(String email) {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, "User already exists with email: " + email);
    }
}