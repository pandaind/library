package com.library.bookservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(unique = true)
    private String isbn;

    private String publisher;

    @Column(name = "publication_year")
    private Integer publicationYear;

    private String genre;

    @Column(name = "total_copies", nullable = false)
    private Integer totalCopies;

    @Column(name = "available_copies", nullable = false)
    private Integer availableCopies;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String language;

    private Integer pages;

    public void borrowBook() {
        if (availableCopies > 0) {
            availableCopies--;
        }
    }

    public void returnBook() {
        if (availableCopies < totalCopies) {
            availableCopies++;
        }
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }
}