package com.busuu.app.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseResponse extends BaseResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("flag_icon_url")
    private String flagIconUrl;

    @JsonProperty("flag_icon_name")
    private String flagIconName;

    @JsonProperty("course_order")
    private Integer courseOrder;

    @JsonProperty("level_ids")
    private List<String> levelIds;

    @JsonProperty("progress")
    private Double progress;

    @JsonProperty("is_completed")
    private Boolean isCompleted;
}
