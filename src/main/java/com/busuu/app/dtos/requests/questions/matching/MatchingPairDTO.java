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

    @JsonProperty("pair_text")
    @NotBlank(message = "Pair text cannot be empty")
    private String pairText;

    @JsonProperty("pair_key")
    @NotBlank(message = "Pair key cannot be empty")
    private String pairKey;

    @JsonProperty("pair_order")
    @NotNull(message = "Pair order is required")
    @Min(value = 1, message = "Pair order must be at least 1")
    private Integer pairOrder;

    @JsonProperty("question_matching_id")
    private String questionMatchingId;
}
