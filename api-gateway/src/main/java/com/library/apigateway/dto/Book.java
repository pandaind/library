package com.library.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String publisher;
    private Integer publicationYear;
    private String genre;
    private Integer totalCopies;
    private Integer availableCopies;
    private String description;
    private String language;
    private Integer pages;
    private List<User> borrowedBy = new ArrayList<>();
    
    public Boolean getIsAvailable() {
        return availableCopies != null && availableCopies > 0;
    }
}