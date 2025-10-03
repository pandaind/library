package com.library.bookservice.exception;

public class InvalidBookDataException extends BookServiceException {
    public InvalidBookDataException(String message) {
        super(ErrorCode.INVALID_BOOK_DATA, message);
    }
    
    public InvalidBookDataException(String field, String reason) {
        super(ErrorCode.INVALID_BOOK_DATA, "Invalid " + field + ": " + reason);
    }
}