package com.busuu.app.dtos.responses.question.ordering;

import com.busuu.app.dtos.responses.question.QuestionResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionOrderingResponse extends QuestionResponse {

    @JsonProperty("correct_answer")
    private String correctAnswer;

    @JsonProperty("parts")
    private List<OrderingPartResponse> parts;
}
