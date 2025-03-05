package com.busuu.app.dtos.requests;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LevelDTO implements Serializable
{

    @NotBlank(message = "Level ID is required!")
    @Size(min = 1, max = 255, message = "Invalid ID length (minimum length is 1 and maximum length is 255!")
    private String id;

    @NotBlank(message = "Level code is required!")
    @Size(min = 1, max = 255, message = "Invalid ID length (minimum length is 1 and maximum length is 255!")
    private String code;

    @NotBlank(message = "Level name is required!")
    @Size(min = 1, max = 255, message = "Invalid ID length (minimum length is 1 and maximum length is 255!")
    private String name;

    @NotBlank(message = "Level description is required!")
    @Size(min = 1, max = 255)
    private String description;

    @NotEmpty(message = "Chapter list is required!")
    private List<ChapterDTO> chapters = new ArrayList<>();

    @NotEmpty(message = "Course level is required!")
    private List<CourseLevelDTO> courseLevels = new ArrayList<>();

}