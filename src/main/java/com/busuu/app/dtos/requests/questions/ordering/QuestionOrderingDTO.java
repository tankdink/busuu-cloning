package com.busuu.app.dtos.requests.questions.ordering;

import com.busuu.app.dtos.requests.questions.QuestionDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionOrderingDTO extends QuestionDTO {
    @JsonProperty("correct_answer")
    @NotBlank(message = "Correct answer cannot be empty")
    private String correctAnswer;

    @JsonProperty("parts")
    private List<OrderingPartDTO> parts;
}
