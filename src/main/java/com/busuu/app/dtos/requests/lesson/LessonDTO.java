package com.busuu.app.dtos.requests.lesson;

import com.busuu.app.dtos.requests.word.WordDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class LessonDTO {

    @JsonProperty("title")
    @NotBlank(message = "Title is required")
    @Size(min = 1, message = "Title must have at least 1 character")
    private String title;

    @JsonProperty("description")
    @NotBlank(message = "Description is required")
    @Size(min = 1, message = "Description must have at least 1 character")
    private String description;

    @JsonProperty("lesson_order")
//    @NotNull(message = "Lesson order is required")
//    @Positive(message = "Lesson order must be greater than 0")
    private Integer lessonOrder;

    @JsonProperty("flag_icon")
    private MultipartFile flagIcon;

    @JsonProperty("chapter_id")
    @NotBlank(message = "Chapter ID is required")
    private String chapterId;

    @JsonProperty("words")
    private List<WordDTO> words;
}
