package com.library.apigateway.mapper;

import com.library.apigateway.dto.Book;
import org.springframework.stereotype.Component;
import java.util.ArrayList;

@Component
public class BookMapper {

    public Book toBook(com.library.bookservice.grpc.Book protoBook) {
        if (protoBook == null) {
            return null;
        }
        
        return Book.builder()
                .id(protoBook.getId())
                .title(protoBook.getTitle())
                .author(protoBook.getAuthor())
                .isbn(protoBook.getIsbn().isEmpty() ? null : protoBook.getIsbn())
                .publisher(protoBook.getPublisher().isEmpty() ? null : protoBook.getPublisher())
                .publicationYear(protoBook.getPublicationYear() == 0 ? null : protoBook.getPublicationYear())
                .genre(protoBook.getGenre().isEmpty() ? null : protoBook.getGenre())
                .totalCopies(protoBook.getTotalCopies())
                .availableCopies(protoBook.getAvailableCopies())
                .description(protoBook.getDescription().isEmpty() ? null : protoBook.getDescription())
                .language(protoBook.getLanguage().isEmpty() ? null : protoBook.getLanguage())
                .pages(protoBook.getPages() == 0 ? null : protoBook.getPages())
                .borrowedBy(new ArrayList<>())
                .build();
    }
}