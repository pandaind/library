package com.library.apigateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.library.apigateway.enums.SearchType;

import jakarta.validation.constraints.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchInput {
    @NotBlank(message = "Search query is required")
    @Size(min = 1, max = 100, message = "Search query must be between 1 and 100 characters")
    private String query;
    
    @NotNull(message = "Search type is required")
    private SearchType searchType = SearchType.ALL;
    
    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 100, message = "Limit must not exceed 100")
    private Integer limit = 10;
    
    @Min(value = 0, message = "Offset must be non-negative")
    private Integer offset = 0;
}