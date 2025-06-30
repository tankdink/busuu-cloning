package com.busuu.app.dtos.requests.questions;

import com.busuu.app.dtos.requests.questions.matching.QuestionMatchingDTO;
import com.busuu.app.dtos.requests.questions.multiple_choice.QuestionMultipleChoiceDTO;
import com.busuu.app.dtos.requests.questions.ordering.QuestionOrderingDTO;
import com.busuu.app.entities.questions.QuestionType;
import com.busuu.app.entities.questions.ShowType;
import com.busuu.app.entities.questions.multiple_choice.QuestionMultipleChoice;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "questionType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = QuestionTrueFalseDTO.class, name = "TRUE_FALSE"),
        @JsonSubTypes.Type(value = QuestionFillBlankDTO.class, name = "FILL_BLANK"),
        @JsonSubTypes.Type(value = QuestionMultipleChoiceDTO.class, name = "MULTI_CHOICE"),
        @JsonSubTypes.Type(value = QuestionOrderingDTO.class, name = "ORDERING"),
        @JsonSubTypes.Type(value = QuestionMatchingDTO.class, name = "MATCHING"),
})
public class QuestionDTO {

    @JsonProperty("request")
    @NotBlank(message = "Request cannot be empty")
    private String request;

    @JsonProperty("question_text")
    @NotBlank(message = "Question text cannot be empty")
    private String questionText;

    @JsonProperty("mark")
//    @NotNull(message = "Mark is required")
//    @Min(value = 1, message = "Mark must be at least 1")
//    @Max(value = 10, message = "Mark cannot exceed 10")
    private Integer mark;

    @JsonProperty("explanation")
    @NotBlank(message = "Explanation cannot be empty")
    private String explanation;

    @JsonProperty("image")
    private MultipartFile image;

    @JsonProperty("video")
    private MultipartFile video;

    @JsonProperty("audio")
    private MultipartFile audio;

    @JsonProperty("question_order")
//    @NotNull(message = "Question order is required")
//    @Min(value = 1, message = "Question order must be at least 1")
    private Integer questionOrder;

    @JsonProperty("lesson_id")
    private String lessonId;

    @JsonProperty("hint")
    private String hint;

    @JsonProperty("grammar_section_id")
    private String grammarSectionId;

    @JsonProperty("question_type")
    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    @JsonProperty("show_type")
    @NotNull(message = "Show type is required")
    private ShowType showType;
}
