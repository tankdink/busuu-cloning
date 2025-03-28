package com.busuu.app.dtos.requests.chapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChapterFindByCourseIdAndLevelIdDTO
{
    @NotBlank(message = "The Course ID to which this chapter belongs is required!")
    @JsonProperty("course_id")
    private String courseId;

    @NotBlank(message = "The Level ID to which this chapter belongs is required")
    @JsonProperty("level_id")
    private String levelId;
}
