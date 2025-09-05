package com.busuu.app.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgressResponse extends BaseResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("object_name")
    private String objectName;

    @JsonProperty("object_id")
    private String objectId;

    @JsonProperty("progress")
    private Double progress;

    @JsonProperty("is_completed")
    private Boolean isCompleted;
}
