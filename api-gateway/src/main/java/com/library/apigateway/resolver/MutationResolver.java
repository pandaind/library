package com.library.apigateway.resolver;

import com.library.apigateway.dto.*;
import com.library.apigateway.exception.ServiceUnavailableException;
import com.library.apigateway.exception.ValidationException;
import com.library.apigateway.mapper.BookMapper;
import com.library.apigateway.mapper.UserMapper;
import com.library.apigateway.validation.InputValidator;
import com.library.bookservice.grpc.*;
import com.library.userservice.grpc.*;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

@Controller
@Slf4j
public class MutationResolver {

    private final BookServiceGrpc.BookServiceBlockingStub bookServiceStub;
    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final InputValidator inputValidator;

    public MutationResolver(BookServiceGrpc.BookServiceBlockingStub bookServiceStub,
                          UserServiceGrpc.UserServiceBlockingStub userServiceStub,
                          BookMapper bookMapper, 
                          UserMapper userMapper,
                          InputValidator inputValidator) {
        this.bookServiceStub = bookServiceStub;
        this.userServiceStub = userServiceStub;
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
        this.inputValidator = inputValidator;
    }

    @MutationMapping
    public com.library.apigateway.dto.UserResponse registerUser(@Argument("input") @Valid UserInput input) {
        log.info("Registering new user with input: {}", input);
        
        // Validate input
        inputValidator.validate(input);
        
        try {
            String username = input.getUsername();
            String email = input.getEmail();
            String firstName = input.getFirstName();
            String lastName = input.getLastName();
            String phone = input.getPhone();
            String address = input.getAddress();
            String membershipType = input.getMembershipType().name();
            
            RegisterRequest.Builder requestBuilder = RegisterRequest.newBuilder()
                    .setUsername(username)
                    .setEmail(email)
                    .setFirstName(firstName)
                    .setLastName(lastName)
                    .setMembershipType(membershipType != null ? membershipType : "BASIC");
            
            if (phone != null) {
                requestBuilder.setPhone(phone);
            }
            if (address != null) {
                requestBuilder.setAddress(address);
            }
            
            com.library.userservice.grpc.UserResponse grpcResponse = 
                    userServiceStub.registerUser(requestBuilder.build());
            
            return com.library.apigateway.dto.UserResponse.builder()
                    .success(grpcResponse.getSuccess())
                    .message(grpcResponse.getMessage())
                    .user(grpcResponse.hasUser() ? userMapper.toUser(grpcResponse.getUser()) : null)
                    .build();
                    
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while registering user: status={}, description={}", 
                     e.getStatus().getCode(), e.getStatus().getDescription());
            
            // Let the GraphQL error handler process this
            throw new ServiceUnavailableException("UserService", e);
            
        } catch (Exception e) {
            log.error("Unexpected error while registering user", e);
            throw new RuntimeException("Failed to register user", e);
        }
    }

    @MutationMapping
    public com.library.apigateway.dto.UserResponse updateUser(@Argument String id, @Argument("input") UserInput input) {
        log.info("Updating user {} with input: {}", id, input);
        
        // Note: This would require implementing an updateUser method in user-service
        return com.library.apigateway.dto.UserResponse.builder()
                .success(false)
                .message("Update user functionality not yet implemented")
                .build();
    }

    @MutationMapping
    public com.library.apigateway.dto.UserResponse suspendUser(@Argument String id) {
        log.info("Suspending user {}", id);
        
        // Note: This would require implementing user status management in user-service
        return com.library.apigateway.dto.UserResponse.builder()
                .success(false)
                .message("User suspension functionality not yet implemented")
                .build();
    }

    @MutationMapping
    public com.library.apigateway.dto.UserResponse activateUser(@Argument String id) {
        log.info("Activating user {}", id);
        
        // Note: This would require implementing user status management in user-service
        return com.library.apigateway.dto.UserResponse.builder()
                .success(false)
                .message("User activation functionality not yet implemented")
                .build();
    }

    @MutationMapping
    public com.library.apigateway.dto.BorrowResponse borrowBook(@Argument("input") BorrowBookInput input) {
        log.info("Processing borrow book request with input: {}", input);
        
        try {
            String bookId = input.getBookId();
            String userId = input.getUserId();
            LocalDateTime dueDate = input.getDueDate();
            
            // Default due date to 14 days from now if not provided
            if (dueDate == null) {
                dueDate = LocalDateTime.now().plusDays(14);
            }
            
            BorrowRequest request = BorrowRequest.newBuilder()
                    .setBookId(bookId)
                    .setUserId(userId)
                    .setBorrowDate(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    .setDueDate(dueDate.toEpochSecond(ZoneOffset.UTC))
                    .build();
            
            com.library.bookservice.grpc.BorrowResponse grpcResponse = 
                    bookServiceStub.borrowBook(request);
            
            com.library.apigateway.dto.BorrowResponse.BorrowResponseBuilder responseBuilder = com.library.apigateway.dto.BorrowResponse.builder()
                    .success(grpcResponse.getSuccess())
                    .message(grpcResponse.getMessage());
            
            if (grpcResponse.getSuccess()) {
                responseBuilder.transactionId(grpcResponse.getTransactionId())
                              .dueDate(LocalDateTime.ofEpochSecond(grpcResponse.getDueDate(), 0, ZoneOffset.UTC));
                
                // Optionally fetch book and user details to include in response
                try {
                    BookRequest bookRequest = BookRequest.newBuilder().setBookId(bookId).build();
                    com.library.bookservice.grpc.BookResponse bookResponse = bookServiceStub.getBook(bookRequest);
                    if (bookResponse.getSuccess() && bookResponse.hasBook()) {
                        responseBuilder.book(bookMapper.toBook(bookResponse.getBook()));
                    }
                } catch (Exception e) {
                    log.warn("Could not fetch book details for response", e);
                }
                
                try {
                    UserRequest userRequest = UserRequest.newBuilder().setUserId(userId).build();
                    com.library.userservice.grpc.UserResponse userResponse = userServiceStub.getUser(userRequest);
                    if (userResponse.getSuccess() && userResponse.hasUser()) {
                        responseBuilder.user(userMapper.toUser(userResponse.getUser()));
                    }
                } catch (Exception e) {
                    log.warn("Could not fetch user details for response", e);
                }
            }
            
            return responseBuilder.build();
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while borrowing book: {}", e.getMessage());
            return com.library.apigateway.dto.BorrowResponse.builder()
                    .success(false)
                    .message("Service temporarily unavailable: " + e.getStatus().getDescription())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error while borrowing book", e);
            return com.library.apigateway.dto.BorrowResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build();
        }
    }

    @MutationMapping
    public com.library.apigateway.dto.ReturnResponse returnBook(@Argument("input") ReturnBookInput input) {
        log.info("Processing return book request with input: {}", input);
        
        try {
            String bookId = input.getBookId();
            String userId = input.getUserId();
            LocalDateTime returnDate = input.getReturnDate();
            
            // Default return date to now if not provided
            if (returnDate == null) {
                returnDate = LocalDateTime.now();
            }
            
            com.library.bookservice.grpc.ReturnRequest request = 
                    com.library.bookservice.grpc.ReturnRequest.newBuilder()
                    .setBookId(bookId)
                    .setUserId(userId)
                    .setReturnDate(returnDate.toEpochSecond(ZoneOffset.UTC))
                    .build();
            
            com.library.bookservice.grpc.ReturnResponse grpcResponse = 
                    bookServiceStub.returnBook(request);
            
            com.library.apigateway.dto.ReturnResponse.ReturnResponseBuilder responseBuilder = com.library.apigateway.dto.ReturnResponse.builder()
                    .success(grpcResponse.getSuccess())
                    .message(grpcResponse.getMessage());
            
            if (grpcResponse.getSuccess()) {
                responseBuilder.transactionId(grpcResponse.getTransactionId())
                              .returnDate(LocalDateTime.ofEpochSecond(grpcResponse.getReturnDate(), 0, ZoneOffset.UTC))
                              .fineAmount((float) grpcResponse.getFineAmount());
                
                // Optionally fetch book and user details to include in response
                try {
                    BookRequest bookRequest = BookRequest.newBuilder().setBookId(bookId).build();
                    com.library.bookservice.grpc.BookResponse bookResponse = bookServiceStub.getBook(bookRequest);
                    if (bookResponse.getSuccess() && bookResponse.hasBook()) {
                        responseBuilder.book(bookMapper.toBook(bookResponse.getBook()));
                    }
                } catch (Exception e) {
                    log.warn("Could not fetch book details for response", e);
                }
                
                try {
                    UserRequest userRequest = UserRequest.newBuilder().setUserId(userId).build();
                    com.library.userservice.grpc.UserResponse userResponse = userServiceStub.getUser(userRequest);
                    if (userResponse.getSuccess() && userResponse.hasUser()) {
                        responseBuilder.user(userMapper.toUser(userResponse.getUser()));
                    }
                } catch (Exception e) {
                    log.warn("Could not fetch user details for response", e);
                }
            }
            
            return responseBuilder.build();
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while returning book: {}", e.getMessage());
            return com.library.apigateway.dto.ReturnResponse.builder()
                    .success(false)
                    .message("Service temporarily unavailable: " + e.getStatus().getDescription())
                    .fineAmount(0.0f)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error while returning book", e);
            return com.library.apigateway.dto.ReturnResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .fineAmount(0.0f)
                    .build();
        }
    }

    @MutationMapping
    public com.library.apigateway.dto.UserResponse payFine(@Argument String userId, @Argument Double amount) {
        log.info("Processing fine payment for user {} amount {}", userId, amount);
        
        // Note: This would require implementing fine management in user-service
        return com.library.apigateway.dto.UserResponse.builder()
                .success(false)
                .message("Fine payment functionality not yet implemented")
                .build();
    }
}