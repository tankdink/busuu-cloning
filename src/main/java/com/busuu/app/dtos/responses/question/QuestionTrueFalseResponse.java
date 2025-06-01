package com.busuu.app.dtos.responses.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class QuestionTrueFalseResponse extends QuestionResponse{
    @JsonProperty("correct_answer")
    private Boolean correctAnswer;
}
