package com.library.apigateway.config;

import com.library.apigateway.dto.Book;
import com.library.apigateway.dto.User;
import com.library.apigateway.mapper.BookMapper;
import com.library.apigateway.mapper.UserMapper;
import com.library.bookservice.grpc.BookRequest;
import com.library.bookservice.grpc.BookResponse;
import com.library.bookservice.grpc.BookServiceGrpc;
import com.library.userservice.grpc.UserRequest;
import com.library.userservice.grpc.UserResponse;
import com.library.userservice.grpc.UserServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoaderConfig {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @GrpcClient("book-service")
    private BookServiceGrpc.BookServiceBlockingStub bookServiceStub;

    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    @Bean
    public DataLoader<String, User> userDataLoader() {
        BatchLoader<String, User> batchLoader = userIds -> {
            log.debug("Batch loading {} users", userIds.size());
            
            return CompletableFuture.supplyAsync(() -> {
                return userIds.stream()
                    .map(userId -> {
                        try {
                            UserRequest request = UserRequest.newBuilder()
                                    .setUserId(userId)
                                    .build();
                            UserResponse response = userServiceStub.getUser(request);
                            
                            if (response.getSuccess() && response.hasUser()) {
                                return userMapper.toUser(response.getUser());
                            }
                            return null;
                        } catch (Exception e) {
                            log.error("Error loading user {}: {}", userId, e.getMessage());
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
            });
        };

        return DataLoader.newDataLoader(batchLoader, 
            DataLoaderOptions.newOptions()
                .setCachingEnabled(true)
                .setBatchingEnabled(true)
        );
    }

    @Bean
    public DataLoader<String, Book> bookDataLoader() {
        BatchLoader<String, Book> batchLoader = bookIds -> {
            log.debug("Batch loading {} books", bookIds.size());
            
            return CompletableFuture.supplyAsync(() -> {
                return bookIds.stream()
                    .map(bookId -> {
                        try {
                            BookRequest request = BookRequest.newBuilder()
                                    .setBookId(bookId)
                                    .build();
                            BookResponse response = bookServiceStub.getBook(request);
                            
                            if (response.getSuccess() && response.hasBook()) {
                                return bookMapper.toBook(response.getBook());
                            }
                            return null;
                        } catch (Exception e) {
                            log.error("Error loading book {}: {}", bookId, e.getMessage());
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
            });
        };

        return DataLoader.newDataLoader(batchLoader, 
            DataLoaderOptions.newOptions()
                .setCachingEnabled(true)
                .setBatchingEnabled(true)
        );
    }
}