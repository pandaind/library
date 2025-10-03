package com.library.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowHistoryResponse {
    private Boolean success;
    private String message;
    private List<BorrowRecord> history;
    private Integer totalCount;
}