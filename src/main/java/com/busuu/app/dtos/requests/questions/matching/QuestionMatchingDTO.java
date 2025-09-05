package com.busuu.app.dtos.requests.questions.matching;

import com.busuu.app.dtos.requests.questions.QuestionDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionMatchingDTO extends QuestionDTO {
    @JsonProperty("pairs")
    private List<MatchingPairDTO> pairs;
}
