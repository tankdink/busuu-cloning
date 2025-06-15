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

    @JsonProperty("part_text")
    private String partText;

    @JsonProperty("pair_key")
    private String pairKey;

    @JsonProperty("part_order")
    private Integer partOrder;
}
