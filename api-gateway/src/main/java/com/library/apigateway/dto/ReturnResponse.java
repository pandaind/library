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
public class ReturnResponse {
    private Boolean success;
    private String message;
    private String transactionId;
    private LocalDateTime returnDate;
    private Float fineAmount;
    private Book book;
    private User user;
}