package com.busuu.app.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagingResponse<T> {

    @JsonProperty("objects")
    private List<T> objects;

    @JsonProperty("total_pages")
    private Integer totalPages;

    @JsonProperty("total_objects")
    private Long totalObjects;
}
