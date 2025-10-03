package com.library.bookservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", unique = true, nullable = false)
    private String transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "borrow_date", nullable = false)
    private LocalDateTime borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus status;

    @Column(name = "fine_amount")
    private Double fineAmount;

    public enum BorrowStatus {
        BORROWED,
        RETURNED,
        OVERDUE
    }

    public boolean isOverdue() {
        return status == BorrowStatus.BORROWED && 
               dueDate.isBefore(LocalDateTime.now());
    }

    public void returnBook() {
        this.returnDate = LocalDateTime.now();
        this.status = BorrowStatus.RETURNED;
    }

    public void markOverdue() {
        if (status == BorrowStatus.BORROWED && isOverdue()) {
            this.status = BorrowStatus.OVERDUE;
        }
    }
}