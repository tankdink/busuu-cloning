package com.busuu.app.dtos.responses.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionFillBlankResponse extends QuestionResponse {

    @JsonProperty("correct_answer")
    private Set<String> correctAnswer = new HashSet<>();
}
