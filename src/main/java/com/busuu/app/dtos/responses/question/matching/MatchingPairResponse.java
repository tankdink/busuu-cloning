package com.busuu.app.dtos.responses.question.matching;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchingPairResponse {

    @JsonProperty("matching_pair_id")
    private String id;

    @JsonProperty("pair_text")
    private String pairText;

    @JsonProperty("pair_key")
    private String pairKey;

    @JsonProperty("pair_order")
    private Integer pairOrder;
}
