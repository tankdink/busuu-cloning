package com.busuu.app.dtos.responses.question.matching;

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
public class QuestionMatchingResponse extends QuestionResponse {

    @JsonProperty("pairs")
    private List<MatchingPairResponse> pairs;
}
