package com.library.apigateway.resolver;

import com.library.apigateway.dto.*;
import com.library.apigateway.mapper.BookMapper;
import com.library.apigateway.dto.BookResponse;
import com.library.bookservice.grpc.*;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.library.apigateway.dto.BookSearchInput;
import com.library.apigateway.enums.SearchType;

@Controller
@Slf4j
public class BookQueryResolver {

    private final BookServiceGrpc.BookServiceBlockingStub bookServiceStub;
    private final BookMapper bookMapper;
    
    public BookQueryResolver(BookServiceGrpc.BookServiceBlockingStub bookServiceStub, BookMapper bookMapper) {
        this.bookServiceStub = bookServiceStub;
        this.bookMapper = bookMapper;
        log.info("BookQueryResolver initialized with bookServiceStub: {} mapper: {}", bookServiceStub, bookMapper);
    }

    @QueryMapping
    public BookResponse book(@Argument("id") String id) {
        System.out.println("RESOLVER METHOD CALLED: book() with id: " + id);
        log.info("Fetching book with id: {}", id);
        
        try {
            BookRequest request = BookRequest.newBuilder()
                    .setBookId(id)
                    .build();
            
            com.library.bookservice.grpc.BookResponse grpcResponse = bookServiceStub.getBook(request);
            
            return BookResponse.builder()
                    .success(grpcResponse.getSuccess())
                    .message(grpcResponse.getMessage())
                    .book(grpcResponse.hasBook() ? bookMapper.toBook(grpcResponse.getBook()) : null)
                    .build();
                    
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while fetching book: {}", e.getMessage());
            return BookResponse.builder()
                    .success(false)
                    .message("Service temporarily unavailable: " + e.getStatus().getDescription())
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error while fetching book", e);
            return BookResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .build();
        }
    }
    
    @QueryMapping
    public String testQuery() {
        System.out.println("TEST QUERY CALLED");
        return "Hello World from GraphQL";
    }

    @QueryMapping
    public BooksResponse books() {
        BookSearchInput input = BookSearchInput.builder()
            .query("")
            .searchType(SearchType.ALL)
            .limit(50)
            .offset(0)
            .build();
        return searchBooks(input);
    }

    @QueryMapping
    public BooksResponse searchBooks(@Argument("input") BookSearchInput input) {
        log.info("Searching books with input: {}", input);
        
        try {
            String query = input.getQuery() != null ? input.getQuery() : "";
            String searchType = input.getSearchType() != null ? input.getSearchType().name().toLowerCase() : "all";
            Integer limit = input.getLimit() != null ? input.getLimit() : 10;
            Integer offset = input.getOffset() != null ? input.getOffset() : 0;
            
            SearchRequest request = SearchRequest.newBuilder()
                    .setQuery(query)
                    .setSearchType(searchType)
                    .setLimit(limit)
                    .setOffset(offset)
                    .build();
            
            List<com.library.apigateway.dto.Book> books = new ArrayList<>();
            
            // Use server streaming to collect all results
            bookServiceStub.searchBooks(request).forEachRemaining(grpcResponse -> {
                if (grpcResponse.getSuccess() && grpcResponse.hasBook()) {
                    books.add(bookMapper.toBook(grpcResponse.getBook()));
                }
            });
            
            return BooksResponse.builder()
                    .success(true)
                    .message("Books retrieved successfully")
                    .books(books)
                    .totalCount(books.size())
                    .build();
                    
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while searching books: {}", e.getMessage());
            return BooksResponse.builder()
                    .success(false)
                    .message("Service temporarily unavailable: " + e.getStatus().getDescription())
                    .books(new ArrayList<>())
                    .totalCount(0)
                    .build();
        } catch (Exception e) {
            log.error("Unexpected error while searching books", e);
            return BooksResponse.builder()
                    .success(false)
                    .message("An unexpected error occurred")
                    .books(new ArrayList<>())
                    .totalCount(0)
                    .build();
        }
    }

    @QueryMapping
    public BooksResponse availableBooks(@Argument Integer limit, @Argument Integer offset) {
        BookSearchInput input = BookSearchInput.builder()
            .query("")
            .searchType(SearchType.ALL)
            .limit(limit != null ? limit : 10)
            .offset(offset != null ? offset : 0)
            .build();
        return searchBooks(input);
    }

    @QueryMapping
    public BooksResponse booksByGenre(@Argument String genre, @Argument Integer limit, @Argument Integer offset) {
        BookSearchInput input = BookSearchInput.builder()
            .query(genre)
            .searchType(SearchType.GENRE)
            .limit(limit != null ? limit : 10)
            .offset(offset != null ? offset : 0)
            .build();
        return searchBooks(input);
    }

    @QueryMapping
    public BooksResponse booksByAuthor(@Argument String author, @Argument Integer limit, @Argument Integer offset) {
        BookSearchInput input = BookSearchInput.builder()
            .query(author)
            .searchType(SearchType.AUTHOR)
            .limit(limit != null ? limit : 10)
            .offset(offset != null ? offset : 0)
            .build();
        return searchBooks(input);
    }

    @QueryMapping
    public Integer totalBooks() {
        // Get all books and count them
        BooksResponse response = books();
        return response.getTotalCount();
    }

    @QueryMapping
    public Integer totalAvailableBooks() {
        try {
            // Search for available books only
            BooksResponse response = availableBooks(1000, 0); // Large limit to get all
            return Math.toIntExact(response.getBooks().stream()
                    .filter(book -> book.getIsAvailable())
                    .count());
        } catch (Exception e) {
            log.error("Error counting available books", e);
            return 0;
        }
    }

    @QueryMapping
    public List<String> bookGenres() {
        try {
            // Get all books and extract unique genres
            BooksResponse response = books();
            return response.getBooks().stream()
                    .map(com.library.apigateway.dto.Book::getGenre)
                    .filter(genre -> genre != null && !genre.trim().isEmpty())
                    .distinct()
                    .sorted()
                    .toList();
        } catch (Exception e) {
            log.error("Error fetching book genres", e);
            return new ArrayList<>();
        }
    }
}