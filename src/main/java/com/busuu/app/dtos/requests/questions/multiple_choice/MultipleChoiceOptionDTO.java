package com.busuu.app.dtos.requests.questions.multiple_choice;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultipleChoiceOptionDTO {

    @JsonProperty("option_text")
    @NotBlank(message = "Option text cannot be empty")
    private String optionText;

    @JsonProperty("is_correct")
    @NotNull(message = "Is correct field is required")
    private Boolean isCorrect;

    @JsonProperty("option_order")
    @NotNull(message = "Option order is required")
    @Min(value = 1, message = "Option order must be at least 1")
    private Integer optionOrder;

    @JsonProperty("question_multiple_choice_id")
    private String questionMultipleChoiceId;
}
