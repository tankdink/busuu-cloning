package com.busuu.app.dtos.responses.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionTrueFalseResponse extends QuestionResponse{
    @JsonProperty("correct_answer")
    private Boolean correctAnswer;
}
