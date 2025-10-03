package com.library.bookservice.exception;

public class BookNotFoundException extends BookServiceException {
    public BookNotFoundException(String bookId) {
        super(ErrorCode.BOOK_NOT_FOUND, "Book not found with ID: " + bookId);
    }
}