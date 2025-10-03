package com.library.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowResponse {
    private Boolean success;
    private String message;
    private String transactionId;
    private LocalDateTime dueDate;
    private Book book;
    private User user;
}