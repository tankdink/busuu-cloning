package com.busuu.app.dtos.requests.chapter;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChapterDTO
{

    @NotBlank(message = "Chapter Title is required!")
    @Size(min = 1, message = "Invalid Title length (minimum length is 1)!")
    private String title;

    @NotBlank(message = "Chapter Description is required!")
    @Size(min = 1, message = "Invalid Description length (minimum length is 1)!")
    private String description;

//    @NotNull(message = "Chapter Order is required!")
//    @Min(value = 1, message = "The Chapter Order must be at least 1")
    @JsonProperty("chapter_order")
    private Integer chapterOrder;

    @NotBlank(message = "The Course ID to which this chapter belongs is required!")
    @JsonProperty("course_id")
    private String courseId;

    @NotBlank(message = "The Level ID to which this chapter belongs is required")
    @JsonProperty("level_id")
    private String levelId;

}