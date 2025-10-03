package com.library.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false)
    private MembershipType membershipType;

    @Column(name = "max_books_allowed", nullable = false)
    private Integer maxBooksAllowed;

    @Column(name = "current_borrowed_books", nullable = false)
    private Integer currentBorrowedBooks;

    @Column(name = "outstanding_fines")
    private Double outstandingFines;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_borrowed_books", 
                     joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "book_id")
    @Builder.Default
    private List<String> borrowedBookIds = new ArrayList<>();

    public enum UserStatus {
        ACTIVE,
        SUSPENDED,
        INACTIVE
    }

    public enum MembershipType {
        BASIC,
        PREMIUM,
        STUDENT
    }

    public boolean canBorrowMoreBooks() {
        return currentBorrowedBooks < maxBooksAllowed && 
               status == UserStatus.ACTIVE;
    }

    public void borrowBook(String bookId) {
        if (canBorrowMoreBooks() && !borrowedBookIds.contains(bookId)) {
            borrowedBookIds.add(bookId);
            currentBorrowedBooks++;
        }
    }

    public void returnBook(String bookId) {
        if (borrowedBookIds.contains(bookId)) {
            borrowedBookIds.remove(bookId);
            if (currentBorrowedBooks > 0) {
                currentBorrowedBooks--;
            }
        }
    }

    public boolean hasBookBorrowed(String bookId) {
        return borrowedBookIds.contains(bookId);
    }

    public void addFine(Double amount) {
        if (outstandingFines == null) {
            outstandingFines = 0.0;
        }
        outstandingFines += amount;
    }

    public void payFine(Double amount) {
        if (outstandingFines != null && outstandingFines >= amount) {
            outstandingFines -= amount;
        }
    }

    public boolean hasFines() {
        return outstandingFines != null && outstandingFines > 0;
    }
}