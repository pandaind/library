package com.library.bookservice.service;

import com.library.bookservice.entity.Book;
import com.library.bookservice.entity.BorrowRecord;
import com.library.bookservice.exception.BookNotFoundException;
import com.library.bookservice.exception.BookNotAvailableException;
import com.library.bookservice.exception.InvalidBookDataException;
import com.library.bookservice.exception.BookServiceException;
import com.library.bookservice.exception.ErrorCode;
import com.library.bookservice.grpc.*;
import com.library.bookservice.repository.BookRepository;
import com.library.bookservice.repository.BorrowRecordRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl extends BookServiceGrpc.BookServiceImplBase {

    private final BookRepository bookRepository;
    private final BorrowRecordRepository borrowRecordRepository;

    @Override
    public void getBook(BookRequest request, StreamObserver<BookResponse> responseObserver) {
        log.info("Received GetBook request for bookId: {}", request.getBookId());

        try {
            // Input validation
            if (!StringUtils.hasText(request.getBookId())) {
                throw new InvalidBookDataException("Book ID cannot be empty");
            }
            
            Long bookId;
            try {
                bookId = Long.parseLong(request.getBookId());
            } catch (NumberFormatException e) {
                throw new InvalidBookDataException("Book ID must be a valid number: " + request.getBookId());
            }
            
            Optional<Book> bookOpt = bookRepository.findById(bookId);

            if (bookOpt.isEmpty()) {
                throw new BookNotFoundException(request.getBookId());
            }
            
            Book book = bookOpt.get();
            BookResponse response = BookResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Book found successfully")
                    .setBook(mapToProtoBook(book))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            log.info("Successfully retrieved book with ID: {}", request.getBookId());

        } catch (BookServiceException e) {
            log.warn("BookService exception for bookId {}: {}", request.getBookId(), e.getMessage());
            throw e; // Let the interceptor handle it
        } catch (Exception e) {
            log.error("Unexpected error retrieving book with ID: {}", request.getBookId(), e);
            throw new BookServiceException(ErrorCode.INTERNAL_ERROR, "Failed to retrieve book", e);
        }
    }

    @Override
    public void searchBooks(SearchRequest request, StreamObserver<BookResponse> responseObserver) {
        log.info("Received SearchBooks request - Query: {}, Type: {}", request.getQuery(), request.getSearchType());

        try {
            int limit = request.getLimit() > 0 ? request.getLimit() : 10;
            int offset = Math.max(0, request.getOffset());
            int page = offset / limit;

            Pageable pageable = PageRequest.of(page, limit);
            Page<Book> books;

            String searchType = request.getSearchType().toLowerCase();
            if (searchType.isEmpty() || searchType.equals("all")) {
                books = bookRepository.searchBooksGeneral(request.getQuery(), pageable);
            } else {
                books = bookRepository.searchBooks(request.getQuery(), searchType, pageable);
            }

            books.getContent().forEach(book -> {
                BookResponse response = BookResponse.newBuilder()
                        .setSuccess(true)
                        .setMessage("Book found")
                        .setBook(mapToProtoBook(book))
                        .build();
                responseObserver.onNext(response);
            });

            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error searching books", e);
            BookResponse response = BookResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error searching books: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    @Transactional
    public void borrowBook(BorrowRequest request, StreamObserver<BorrowResponse> responseObserver) {
        log.info("Received BorrowBook request - BookId: {}, UserId: {}", 
                request.getBookId(), request.getUserId());

        try {
            Long bookId = Long.parseLong(request.getBookId());
            Optional<Book> bookOpt = bookRepository.findById(bookId);

            BorrowResponse.Builder responseBuilder = BorrowResponse.newBuilder();

            if (!bookOpt.isPresent()) {
                responseBuilder.setSuccess(false)
                             .setMessage("Book not found");
            } else {
                Book book = bookOpt.get();
                
                // Check if book is available
                if (!book.isAvailable()) {
                    responseBuilder.setSuccess(false)
                                 .setMessage("Book is not available for borrowing");
                } else {
                    // Check if user already has this book borrowed
                    Optional<BorrowRecord> existingBorrow = borrowRecordRepository
                            .findActiveBorrowRecord(request.getUserId(), bookId);
                    
                    if (existingBorrow.isPresent()) {
                        responseBuilder.setSuccess(false)
                                     .setMessage("User already has this book borrowed");
                    } else {
                        // Create borrow record
                        String transactionId = UUID.randomUUID().toString();
                        LocalDateTime borrowDate = LocalDateTime.ofEpochSecond(
                                request.getBorrowDate(), 0, ZoneOffset.UTC);
                        LocalDateTime dueDate = LocalDateTime.ofEpochSecond(
                                request.getDueDate(), 0, ZoneOffset.UTC);

                        BorrowRecord borrowRecord = BorrowRecord.builder()
                                .transactionId(transactionId)
                                .book(book)
                                .userId(request.getUserId())
                                .borrowDate(borrowDate)
                                .dueDate(dueDate)
                                .status(BorrowRecord.BorrowStatus.BORROWED)
                                .fineAmount(0.0)
                                .build();

                        // Update book availability
                        book.borrowBook();
                        
                        // Save records
                        borrowRecordRepository.save(borrowRecord);
                        bookRepository.save(book);

                        responseBuilder.setSuccess(true)
                                     .setMessage("Book borrowed successfully")
                                     .setTransactionId(transactionId)
                                     .setDueDate(request.getDueDate());
                    }
                }
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error borrowing book", e);
            BorrowResponse response = BorrowResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error borrowing book: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    @Transactional
    public void returnBook(ReturnRequest request, StreamObserver<ReturnResponse> responseObserver) {
        log.info("Received ReturnBook request - BookId: {}, UserId: {}", 
                request.getBookId(), request.getUserId());

        try {
            Long bookId = Long.parseLong(request.getBookId());
            Optional<BorrowRecord> borrowRecordOpt = borrowRecordRepository
                    .findActiveBorrowRecord(request.getUserId(), bookId);

            ReturnResponse.Builder responseBuilder = ReturnResponse.newBuilder();

            if (!borrowRecordOpt.isPresent()) {
                responseBuilder.setSuccess(false)
                             .setMessage("No active borrow record found for this book and user");
            } else {
                BorrowRecord borrowRecord = borrowRecordOpt.get();
                Book book = borrowRecord.getBook();

                LocalDateTime returnDate = LocalDateTime.ofEpochSecond(
                        request.getReturnDate(), 0, ZoneOffset.UTC);

                // Calculate fine if overdue
                double fineAmount = 0.0;
                if (returnDate.isAfter(borrowRecord.getDueDate())) {
                    long overdueDays = java.time.Duration.between(
                            borrowRecord.getDueDate(), returnDate).toDays();
                    fineAmount = overdueDays * 1.0; // $1 per day fine
                }

                // Update borrow record
                borrowRecord.setReturnDate(returnDate);
                borrowRecord.setStatus(BorrowRecord.BorrowStatus.RETURNED);
                borrowRecord.setFineAmount(fineAmount);

                // Update book availability
                book.returnBook();

                // Save records
                borrowRecordRepository.save(borrowRecord);
                bookRepository.save(book);

                responseBuilder.setSuccess(true)
                             .setMessage("Book returned successfully")
                             .setTransactionId(borrowRecord.getTransactionId())
                             .setReturnDate(request.getReturnDate())
                             .setFineAmount(fineAmount);
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error returning book", e);
            ReturnResponse response = ReturnResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error returning book: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getBorrowers(GetBorrowersRequest request, StreamObserver<GetBorrowersResponse> responseObserver) {
        log.info("Received GetBorrowers request for bookId: {}", request.getBookId());

        try {
            Long bookId = Long.parseLong(request.getBookId());
            
            // Find all active borrow records for this book
            java.util.List<BorrowRecord> activeBorrowRecords = borrowRecordRepository
                    .findByBookIdAndStatus(bookId, BorrowRecord.BorrowStatus.BORROWED);

            GetBorrowersResponse.Builder responseBuilder = GetBorrowersResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Borrowers retrieved successfully");

            // Extract user IDs from borrow records
            activeBorrowRecords.forEach(record -> 
                    responseBuilder.addUserIds(record.getUserId()));

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error getting borrowers for book", e);
            GetBorrowersResponse response = GetBorrowersResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error getting borrowers: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    private com.library.bookservice.grpc.Book mapToProtoBook(Book book) {
        return com.library.bookservice.grpc.Book.newBuilder()
                .setId(book.getId().toString())
                .setTitle(book.getTitle())
                .setAuthor(book.getAuthor())
                .setIsbn(book.getIsbn() != null ? book.getIsbn() : "")
                .setPublisher(book.getPublisher() != null ? book.getPublisher() : "")
                .setPublicationYear(book.getPublicationYear() != null ? book.getPublicationYear() : 0)
                .setGenre(book.getGenre() != null ? book.getGenre() : "")
                .setTotalCopies(book.getTotalCopies())
                .setAvailableCopies(book.getAvailableCopies())
                .setDescription(book.getDescription() != null ? book.getDescription() : "")
                .setLanguage(book.getLanguage() != null ? book.getLanguage() : "")
                .setPages(book.getPages() != null ? book.getPages() : 0)
                .build();
    }
}