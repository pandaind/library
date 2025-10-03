package com.library.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowBookInput {
    @NotBlank(message = "Book ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\-]+$", message = "Book ID must contain only alphanumeric characters and hyphens")
    private String bookId;
    
    @NotBlank(message = "User ID is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\-]+$", message = "User ID must contain only alphanumeric characters and hyphens")
    private String userId;
    
    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;
}