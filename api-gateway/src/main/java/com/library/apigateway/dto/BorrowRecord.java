package com.library.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import com.library.apigateway.enums.BorrowStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord {
    private String transactionId;
    private String bookId;
    private String bookTitle;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private BorrowStatus status;
    private Float fineAmount;
    
    public Boolean getIsOverdue() {
        return BorrowStatus.BORROWED.equals(status) && 
               dueDate != null && 
               dueDate.isBefore(LocalDateTime.now());
    }
}