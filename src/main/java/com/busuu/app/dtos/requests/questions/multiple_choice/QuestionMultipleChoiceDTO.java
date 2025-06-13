package com.busuu.app.dtos.requests.questions.multiple_choice;

import com.busuu.app.dtos.requests.questions.QuestionDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionMultipleChoiceDTO extends QuestionDTO {
    @JsonProperty("options")
    private List<MultipleChoiceOptionDTO> options;
}
