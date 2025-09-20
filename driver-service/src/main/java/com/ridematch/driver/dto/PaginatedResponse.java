package com.ridematch.driver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginatedResponse<T> {
    private List<T> pageResults;
    private int currentPage;
    private int perPage;
    private long totalRecords;
    private int totalPages;
}
