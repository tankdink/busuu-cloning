package com.busuu.app.dtos.requests.questions.matching;

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
public class MatchingPairDTO {

    @JsonProperty("part_text")
    @NotBlank(message = "Part text cannot be empty")
    private String partText;

    @JsonProperty("pair_key")
    @NotBlank(message = "Pair key cannot be empty")
    private String pairKey;

    @JsonProperty("part_order")
    @NotNull(message = "Part order is required")
    @Min(value = 1, message = "Part order must be at least 1")
    private Integer partOrder;

    @JsonProperty("question_matching_id")
    @NotBlank(message = "Question matching ID cannot be empty")
    private String questionMatchingId;
}
