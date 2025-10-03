package com.library.userservice.service;

import com.library.userservice.entity.User;
import com.library.userservice.grpc.*;
import com.library.userservice.repository.UserRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void registerUser(RegisterRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("Received RegisterUser request for email: {}", request.getEmail());

        try {
            UserResponse.Builder responseBuilder = UserResponse.newBuilder();

            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                responseBuilder.setSuccess(false)
                             .setMessage("User with this email already exists");
            }
            // Check if username already exists
            else if (userRepository.existsByUsername(request.getUsername())) {
                responseBuilder.setSuccess(false)
                             .setMessage("User with this username already exists");
            }
            else {
                // Create new user
                User.MembershipType membershipType;
                try {
                    membershipType = User.MembershipType.valueOf(request.getMembershipType().toUpperCase());
                } catch (IllegalArgumentException e) {
                    membershipType = User.MembershipType.BASIC; // Default to BASIC
                }

                int maxBooksAllowed = getMaxBooksForMembership(membershipType);

                User user = User.builder()
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .phone(request.getPhone())
                        .address(request.getAddress())
                        .registrationDate(LocalDateTime.now())
                        .status(User.UserStatus.ACTIVE)
                        .membershipType(membershipType)
                        .maxBooksAllowed(maxBooksAllowed)
                        .currentBorrowedBooks(0)
                        .outstandingFines(0.0)
                        .borrowedBookIds(new ArrayList<>())
                        .build();

                User savedUser = userRepository.save(user);

                responseBuilder.setSuccess(true)
                             .setMessage("User registered successfully")
                             .setUser(mapToProtoUser(savedUser));
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error registering user", e);
            UserResponse response = UserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error registering user: " + e.getMessage())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        log.info("Received GetUser request for userId: {}", request.getUserId());

        try {
            Long userId = Long.parseLong(request.getUserId());
            Optional<User> userOpt = userRepository.findById(userId);

            UserResponse.Builder responseBuilder = UserResponse.newBuilder();

            if (userOpt.isPresent()) {
                User user = userOpt.get();
                responseBuilder.setSuccess(true)
                             .setMessage("User found successfully")
                             .setUser(mapToProtoUser(user));
            } else {
                responseBuilder.setSuccess(false)
                             .setMessage("User not found with id: " + request.getUserId());
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (NumberFormatException e) {
            log.error("Invalid user ID format: {}", request.getUserId());
            UserResponse response = UserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Invalid user ID format")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error retrieving user", e);
            UserResponse response = UserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Internal server error")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getBorrowHistory(BorrowHistoryRequest request, StreamObserver<BorrowHistoryResponse> responseObserver) {
        log.info("Received GetBorrowHistory request for userId: {}", request.getUserId());

        try {
            Long userId = Long.parseLong(request.getUserId());
            Optional<User> userOpt = userRepository.findById(userId);

            BorrowHistoryResponse.Builder responseBuilder = BorrowHistoryResponse.newBuilder();

            if (!userOpt.isPresent()) {
                responseBuilder.setSuccess(false)
                             .setMessage("User not found with id: " + request.getUserId())
                             .setTotalCount(0);
            } else {
                User user = userOpt.get();
                
                // For this implementation, we'll create mock borrow history based on borrowedBookIds
                List<BorrowHistory> historyList = createMockBorrowHistory(user, request);

                responseBuilder.setSuccess(true)
                             .setMessage("Borrow history retrieved successfully")
                             .addAllHistory(historyList)
                             .setTotalCount(historyList.size());
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error retrieving borrow history", e);
            BorrowHistoryResponse response = BorrowHistoryResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error retrieving borrow history: " + e.getMessage())
                    .setTotalCount(0)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    private com.library.userservice.grpc.User mapToProtoUser(User user) {
        return com.library.userservice.grpc.User.newBuilder()
                .setId(user.getId().toString())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setPhone(user.getPhone() != null ? user.getPhone() : "")
                .setAddress(user.getAddress() != null ? user.getAddress() : "")
                .setRegistrationDate(user.getRegistrationDate().toEpochSecond(ZoneOffset.UTC))
                .setStatus(user.getStatus().name())
                .setMembershipType(user.getMembershipType().name())
                .setMaxBooksAllowed(user.getMaxBooksAllowed())
                .setCurrentBorrowedBooks(user.getCurrentBorrowedBooks())
                .setOutstandingFines(user.getOutstandingFines() != null ? user.getOutstandingFines() : 0.0)
                .build();
    }

    private List<BorrowHistory> createMockBorrowHistory(User user, BorrowHistoryRequest request) {
        List<BorrowHistory> historyList = new ArrayList<>();
        
        // Create entries for currently borrowed books
        for (String bookId : user.getBorrowedBookIds()) {
            BorrowHistory history = BorrowHistory.newBuilder()
                    .setTransactionId("TXN-" + user.getId() + "-" + bookId + "-" + System.currentTimeMillis())
                    .setBookId(bookId)
                    .setBookTitle("Book Title " + bookId) // Mock title
                    .setBorrowDate(LocalDateTime.now().minusDays(7).toEpochSecond(ZoneOffset.UTC))
                    .setDueDate(LocalDateTime.now().plusDays(7).toEpochSecond(ZoneOffset.UTC))
                    .setReturnDate(0) // Not returned yet
                    .setStatus("BORROWED")
                    .setFineAmount(0.0)
                    .build();
            
            historyList.add(history);
        }

        // Add some mock historical entries (returned books)
        if (user.getId() != null) {
            for (int i = 1; i <= 2; i++) { // Add 2 historical entries
                BorrowHistory history = BorrowHistory.newBuilder()
                        .setTransactionId("TXN-" + user.getId() + "-HIST-" + i)
                        .setBookId("HIST-" + i)
                        .setBookTitle("Historical Book " + i)
                        .setBorrowDate(LocalDateTime.now().minusDays(30 + i * 7).toEpochSecond(ZoneOffset.UTC))
                        .setDueDate(LocalDateTime.now().minusDays(16 + i * 7).toEpochSecond(ZoneOffset.UTC))
                        .setReturnDate(LocalDateTime.now().minusDays(14 + i * 7).toEpochSecond(ZoneOffset.UTC))
                        .setStatus("RETURNED")
                        .setFineAmount(i == 2 ? 2.0 : 0.0) // Second book had a fine
                        .build();
                
                historyList.add(history);
            }
        }

        // Apply filtering if specified
        String statusFilter = request.getStatusFilter();
        if (statusFilter != null && !statusFilter.isEmpty() && !statusFilter.equalsIgnoreCase("ALL")) {
            historyList = historyList.stream()
                    .filter(h -> h.getStatus().equalsIgnoreCase(statusFilter))
                    .toList();
        }

        // Apply pagination
        int limit = request.getLimit() > 0 ? request.getLimit() : historyList.size();
        int offset = Math.max(0, request.getOffset());
        int endIndex = Math.min(offset + limit, historyList.size());
        
        if (offset < historyList.size()) {
            return historyList.subList(offset, endIndex);
        } else {
            return new ArrayList<>();
        }
    }

    private int getMaxBooksForMembership(User.MembershipType membershipType) {
        return switch (membershipType) {
            case BASIC -> 3;
            case STUDENT -> 5;
            case PREMIUM -> 10;
        };
    }
}