package com.busuu.app.dtos.responses.question.ordering;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderingPartResponse {

    @JsonProperty("ordering_part_id")
    private String id;

    @JsonProperty("sentence_part")
    private String sentencePart;

//    @JsonProperty("correct_order")
//    private Integer correctOrder;

    @JsonProperty("part_order")
    private Integer partOrder;

    @JsonProperty("question_id")
    private String questionId;
}
