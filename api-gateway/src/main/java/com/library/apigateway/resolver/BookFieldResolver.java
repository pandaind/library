package com.library.apigateway.resolver;

import com.library.apigateway.dto.Book;
import com.library.apigateway.dto.User;
import com.library.bookservice.grpc.BookServiceGrpc;
import com.library.bookservice.grpc.GetBorrowersRequest;
import com.library.bookservice.grpc.GetBorrowersResponse;
import com.library.apigateway.mapper.UserMapper;
import graphql.schema.DataFetchingEnvironment;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dataloader.DataLoader;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Controller
@RequiredArgsConstructor
@Slf4j
public class BookFieldResolver {

    private final BookServiceGrpc.BookServiceBlockingStub bookServiceStub;
    private final UserMapper userMapper;

    @SchemaMapping(typeName = "Book", field = "borrowedBy")
    public CompletableFuture<List<User>> borrowedBy(Book book, DataFetchingEnvironment environment) {
        log.debug("Fetching users who borrowed book: {}", book.getId());
        
        DataLoader<String, User> userDataLoader = environment.getDataLoader("userDataLoader");
        
        try {
            // First, get the list of user IDs who have borrowed this book
            GetBorrowersRequest request = GetBorrowersRequest.newBuilder()
                    .setBookId(book.getId())
                    .build();
            
            GetBorrowersResponse response = bookServiceStub.getBorrowers(request);
            
            if (response.getSuccess()) {
                List<String> userIds = response.getUserIdsList();
                
                if (userIds.isEmpty()) {
                    return CompletableFuture.completedFuture(new ArrayList<>());
                }
                
                // Use DataLoader to batch load users efficiently
                List<CompletableFuture<User>> userFutures = userIds.stream()
                        .map(userId -> userDataLoader.load(userId))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
                
                return CompletableFuture.allOf(userFutures.toArray(new CompletableFuture[0]))
                        .thenApply(v -> userFutures.stream()
                                .map(CompletableFuture::join)
                                .filter(user -> user != null)
                                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
            } else {
                log.warn("Failed to get borrowers for book {}: {}", book.getId(), response.getMessage());
                return CompletableFuture.completedFuture(new ArrayList<>());
            }
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while fetching borrowers for book {}: {}", book.getId(), e.getMessage());
            return CompletableFuture.completedFuture(new ArrayList<>());
        } catch (Exception e) {
            log.error("Unexpected error while fetching borrowers for book {}", book.getId(), e);
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
    }
}