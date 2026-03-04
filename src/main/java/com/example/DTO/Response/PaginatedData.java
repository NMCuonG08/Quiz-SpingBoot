package com.example.DTO.Response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedData<T> {
    private List<T> items;
    private PaginationMeta meta;

    @com.fasterxml.jackson.annotation.JsonProperty("pagination")
    public PaginationMeta getPagination() {
        return meta;
    }
}
