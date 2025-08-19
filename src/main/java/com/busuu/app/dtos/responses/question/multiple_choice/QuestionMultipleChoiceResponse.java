package com.busuu.app.dtos.responses.question.multiple_choice;

import com.busuu.app.dtos.responses.question.QuestionResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionMultipleChoiceResponse extends QuestionResponse {

    @JsonProperty("options")
    private List<MultipleChoiceOptionResponse> options;
}
