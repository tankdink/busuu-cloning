package com.busuu.app.dtos.responses.question.multiple_choice;

import com.busuu.app.dtos.responses.question.QuestionResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class QuestionMultipleChoiceResponse extends QuestionResponse {

    @JsonProperty("options")
    private List<MultipleChoiceOptionResponse> options;
}
