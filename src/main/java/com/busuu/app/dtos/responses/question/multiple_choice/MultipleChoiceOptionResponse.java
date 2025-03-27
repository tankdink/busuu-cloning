package com.busuu.app.dtos.responses.question.multiple_choice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultipleChoiceOptionResponse {

    @JsonProperty("multiple_choice_option_id")
    private String id;

    @JsonProperty("option_text")
    private String optionText;

    @JsonProperty("is_correct")
    private Boolean isCorrect;

    @JsonProperty("option_order")
    private Integer optionOrder;

    @JsonProperty("questionId")
    private String questionId;
}
