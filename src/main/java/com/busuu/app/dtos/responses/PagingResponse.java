package com.busuu.app.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponse<T> {

    @JsonProperty("objects")
    private List<T> objects;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("total_objects")
    private long totalObjects;
}
