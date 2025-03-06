package com.busuu.app.dtos.requests.chapter;


import jakarta.validation.constraints.NotBlank;
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
    @Size(min = 1, message = "Invalid ID length (minimum length is 1)!")
    private String title;

    @NotBlank(message = "Chapter Description is required!")
    @Size(min = 1, message = "Invalid ID length (minimum length is 1)!")
    private String description;

    @NotBlank(message = "Chapter Order is required!")
    private Integer chapterOrder;

    @NotBlank(message = "The Course ID to which this chapter belongs is required!")
    private String course_id;

    @NotBlank(message = "The Level ID to which this chapter belongs is required")
    private String level_id;

}