package com.busuu.app.dtos.requests.questions;


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
public class QuestionFillBlankDTO extends QuestionDTO {

    @JsonProperty("correct_answer")
    @NotBlank(message = "Correct answer cannot be empty")
    private String correctAnswer;
}
