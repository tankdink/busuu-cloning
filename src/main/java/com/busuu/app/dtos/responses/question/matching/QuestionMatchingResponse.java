package com.busuu.app.dtos.responses.question.matching;

import com.busuu.app.dtos.responses.question.QuestionResponse;
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
public class QuestionMatchingResponse extends QuestionResponse {

    @JsonProperty("pairs")
    private List<MatchingPairResponse> pairs;
}
