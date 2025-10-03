package com.library.apigateway.resolver;

import com.library.apigateway.dto.Book;
import com.library.apigateway.dto.User;
import graphql.schema.DataFetchingEnvironment;
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
public class UserFieldResolver {

    @SchemaMapping(typeName = "User", field = "borrowedBooks")
    public CompletableFuture<List<Book>> borrowedBooks(User user, DataFetchingEnvironment environment) {
        log.debug("Fetching borrowed books for user: {}", user.getId());
        
        DataLoader<String, Book> bookDataLoader = environment.getDataLoader("bookDataLoader");
        
        try {
            List<String> borrowedBookIds = user.getBorrowedBookIds();
            
            if (borrowedBookIds.isEmpty()) {
                return CompletableFuture.completedFuture(new ArrayList<>());
            }
            
            // Use DataLoader to batch load books efficiently
            List<CompletableFuture<Book>> bookFutures = borrowedBookIds.stream()
                    .map(bookId -> bookDataLoader.load(bookId))
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            
            return CompletableFuture.allOf(bookFutures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> bookFutures.stream()
                            .map(CompletableFuture::join)
                            .filter(book -> book != null)
                            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
            
        } catch (Exception e) {
            log.error("Unexpected error while fetching borrowed books for user {}", user.getId(), e);
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
    }
}