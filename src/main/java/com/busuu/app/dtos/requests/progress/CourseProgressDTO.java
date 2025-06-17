package com.busuu.app.dtos.requests.progress;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseProgressDTO {
    @JsonProperty("course_id")
    @NotBlank(message = "Course's ID is required")
    private String courseId;

    @JsonProperty("level_id")
    @NotBlank(message = "Level's ID is required")
    private String levelId;

    @JsonProperty("chapter_id")
    @NotBlank(message = "Chapter's ID is required")
    private String chapterId;

    @JsonProperty("lesson_id")
    @NotBlank(message = "Lesson's ID is required")
    private String lessonId;

    @JsonProperty("number_questions")
    @NotNull(message = "Number question is required")
    private Integer numberQuestions;
}
