package com.library.apigateway.resolver;

import com.library.apigateway.dto.*;
import com.library.apigateway.mapper.UserMapper;
import com.library.userservice.grpc.*;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class UserQueryResolver {

    private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;
    private final UserMapper userMapper;

    public UserQueryResolver(UserServiceGrpc.UserServiceBlockingStub userServiceStub, 
                           UserMapper userMapper) {
        this.userServiceStub = userServiceStub;
        this.userMapper = userMapper;
    }

    @QueryMapping
    public com.library.apigateway.dto.UserResponse user(@Argument String id) {
        log.info("Fetching user with id: {}", id);
        
        try {
            UserRequest request = UserRequest.newBuilder()
                    .setUserId(id)
                    .build();
            
            com.library.userservice.grpc.UserResponse grpcResponse = userServiceStub.getUser(request);
            
            return com.library.apigateway.dto.UserResponse.builder()
                    .success(grpcResponse.getSuccess())
                    .message(grpcResponse.getMessage())
                    .user(grpcResponse.hasUser() ? userMapper.toUser(grpcResponse.getUser()) : null)
                    .build();
                    
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while fetching user: {}", e.getMessage());
            return com.library.apigateway.dto.UserResponse.builder()
                    .success(false)
                    .message("Service temporarily unavailable: " + e.getStatus().getDescription())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error while fetching user", e);
            return com.library.apigateway.dto.UserResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build();
        }
    }

    @QueryMapping
    public List<com.library.apigateway.dto.User> users() {
        log.info("Fetching all users");
        
        // Note: This is a simplified implementation since user-service doesn't have a "get all users" method
        // In a real implementation, you might want to add this method to the user service
        // For now, returning an empty list with a log message
        log.warn("Get all users not implemented in user-service gRPC interface");
        return new ArrayList<>();
    }

    @QueryMapping
    public com.library.apigateway.dto.UserResponse userByEmail(@Argument String email) {
        log.info("Fetching user by email: {}", email);
        
        // Note: This would require adding a getUserByEmail method to the user service
        // For now, returning a not implemented response
        return com.library.apigateway.dto.UserResponse.builder()
                .success(false)
                .message("Search by email not yet implemented")
                .build();
    }

    @QueryMapping
    public com.library.apigateway.dto.UserResponse userByUsername(@Argument String username) {
        log.info("Fetching user by username: {}", username);
        
        // Note: This would require adding a getUserByUsername method to the user service
        // For now, returning a not implemented response
        return com.library.apigateway.dto.UserResponse.builder()
                .success(false)
                .message("Search by username not yet implemented")
                .build();
    }

    @QueryMapping
    public List<com.library.apigateway.dto.User> activeUsers() {
        log.info("Fetching active users");
        
        // Note: This would require adding a getActiveUsers method to the user service
        log.warn("Get active users not implemented in user-service gRPC interface");
        return new ArrayList<>();
    }

    @QueryMapping
    public List<com.library.apigateway.dto.User> usersWithFines() {
        log.info("Fetching users with fines");
        
        // Note: This would require adding a getUsersWithFines method to the user service
        log.warn("Get users with fines not implemented in user-service gRPC interface");
        return new ArrayList<>();
    }

    @QueryMapping
    public com.library.apigateway.dto.BorrowHistoryResponse borrowHistory(@Argument("input") Map<String, Object> input) {
        log.info("Fetching borrow history with input: {}", input);
        
        try {
            String userId = (String) input.get("userId");
            String statusFilter = (String) input.get("statusFilter");
            Integer limit = (Integer) input.getOrDefault("limit", 10);
            Integer offset = (Integer) input.getOrDefault("offset", 0);
            
            BorrowHistoryRequest.Builder requestBuilder = BorrowHistoryRequest.newBuilder()
                    .setUserId(userId)
                    .setLimit(limit)
                    .setOffset(offset);
            
            if (statusFilter != null && !statusFilter.isEmpty()) {
                requestBuilder.setStatusFilter(statusFilter);
            }
            
            com.library.userservice.grpc.BorrowHistoryResponse grpcResponse = 
                    userServiceStub.getBorrowHistory(requestBuilder.build());
            
            return com.library.apigateway.dto.BorrowHistoryResponse.builder()
                    .success(grpcResponse.getSuccess())
                    .message(grpcResponse.getMessage())
                    .history(userMapper.toBorrowRecords(grpcResponse.getHistoryList()))
                    .totalCount(grpcResponse.getTotalCount())
                    .build();
                    
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while fetching borrow history: {}", e.getMessage());
            return com.library.apigateway.dto.BorrowHistoryResponse.builder()
                    .success(false)
                    .message("Service temporarily unavailable: " + e.getStatus().getDescription())
                    .history(new ArrayList<>())
                    .totalCount(0)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error while fetching borrow history", e);
            return com.library.apigateway.dto.BorrowHistoryResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .history(new ArrayList<>())
                    .totalCount(0)
                    .build();
        }
    }

    @QueryMapping
    public com.library.apigateway.dto.BorrowHistoryResponse userBorrowHistory(@Argument String userId, @Argument Integer limit, @Argument Integer offset) {
        return borrowHistory(Map.of(
            "userId", userId,
            "limit", limit != null ? limit : 10,
            "offset", offset != null ? offset : 0
        ));
    }

    @QueryMapping
    public com.library.apigateway.dto.BorrowHistoryResponse overdueBooks() {
        log.info("Fetching overdue books");
        
        // Note: This would require a system-wide query across all users
        // For now, returning an empty response
        return com.library.apigateway.dto.BorrowHistoryResponse.builder()
                .success(false)
                .message("Overdue books query not yet implemented")
                .history(new ArrayList<>())
                .totalCount(0)
                .build();
    }

    @QueryMapping
    public Integer totalUsers() {
        log.info("Counting total users");
        
        // Note: This would require adding a count method to the user service
        log.warn("Count total users not implemented in user-service gRPC interface");
        return 0;
    }

    @QueryMapping
    public Integer totalActiveUsers() {
        log.info("Counting active users");
        
        // Note: This would require adding a count active users method to the user service
        log.warn("Count active users not implemented in user-service gRPC interface");
        return 0;
    }
}