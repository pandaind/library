package com.library.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import com.library.apigateway.enums.UserStatus;
import com.library.apigateway.enums.MembershipType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private LocalDateTime registrationDate;
    private UserStatus status;
    private MembershipType membershipType;
    private Integer maxBooksAllowed;
    private Integer currentBorrowedBooks;
    private Float outstandingFines;
    private List<String> borrowedBookIds = new ArrayList<>();
    private List<Book> borrowedBooks = new ArrayList<>();
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public Boolean getCanBorrowMore() {
        return currentBorrowedBooks != null && maxBooksAllowed != null &&
               currentBorrowedBooks < maxBooksAllowed && 
               UserStatus.ACTIVE.equals(status);
    }
    
    public Boolean getHasFines() {
        return outstandingFines != null && outstandingFines > 0.0f;
    }
}