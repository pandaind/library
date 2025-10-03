package com.library.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.library.apigateway.enums.BorrowStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowHistoryInput {
    private String userId;
    private BorrowStatus statusFilter;
    private Integer limit = 10;
    private Integer offset = 0;
}