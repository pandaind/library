package com.library.bookservice.exception;

public enum ErrorCode {
    BOOK_NOT_FOUND("BOOK_001", "Book not found"),
    BOOK_ALREADY_EXISTS("BOOK_002", "Book already exists"),
    BOOK_NOT_AVAILABLE("BOOK_003", "Book is not available for borrowing"),
    BOOK_ALREADY_BORROWED("BOOK_004", "Book is already borrowed"),
    INVALID_BOOK_DATA("BOOK_005", "Invalid book data provided"),
    INSUFFICIENT_COPIES("BOOK_006", "Insufficient book copies available"),
    DATABASE_ERROR("BOOK_007", "Database operation failed"),
    VALIDATION_ERROR("BOOK_008", "Input validation failed"),
    INTERNAL_ERROR("BOOK_009", "Internal server error");
    
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