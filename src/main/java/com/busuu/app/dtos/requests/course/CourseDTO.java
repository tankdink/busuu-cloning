package com.busuu.app.dtos.requests.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDTO {

    @JsonProperty("title")
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @JsonProperty("description")
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @JsonProperty("flag_icon")
    private MultipartFile flagIcon;

    @JsonProperty("course_order")
//    @NotNull(message = "Course order is required")
//    @Min(value = 1, message = "Course order must be at least 1")
    private Integer courseOrder;

    @JsonProperty("level_ids")
    @NotBlank(message = "Level ids is required")
    private String levelIds;
}
