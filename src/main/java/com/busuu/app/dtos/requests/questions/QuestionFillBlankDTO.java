package com.busuu.app.dtos.requests.questions;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionFillBlankDTO extends QuestionDTO {

    @JsonProperty("correct_answer")
    @NotBlank(message = "Correct answer cannot be empty")
    private String correctAnswer;
}
