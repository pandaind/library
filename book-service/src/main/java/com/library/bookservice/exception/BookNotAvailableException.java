package com.library.bookservice.exception;

public class BookNotAvailableException extends BookServiceException {
    public BookNotAvailableException(String bookId) {
        super(ErrorCode.BOOK_NOT_AVAILABLE, "Book is not available for borrowing: " + bookId);
    }
}