package com.busuu.app.dtos.responses.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionFillBlankResponse extends QuestionResponse {

    @JsonProperty("correct_answer")
    private String correctAnswer;
}
