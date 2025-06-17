package com.busuu.app.dtos.responses;

import com.busuu.app.entities.Chapter;
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
public class LevelResponse extends BaseResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("progress")
    private Double progress;

    @JsonProperty("is_completed")
    private Boolean isCompleted;
}
