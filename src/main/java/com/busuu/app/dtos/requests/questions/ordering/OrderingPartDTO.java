package com.busuu.app.dtos.requests.questions.ordering;

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
public class OrderingPartDTO {

    @JsonProperty("sentence_part")
    @NotBlank(message = "Sentence part cannot be empty")
    private String sentencePart;

    @JsonProperty("correct_order")
//    @NotNull(message = "Correct order is required")
//    @Min(value = 1, message = "Correct order must be at least 1")
    private Integer correctOrder;

    @JsonProperty("part_order")
//    @NotNull(message = "Part order is required")
//    @Min(value = 1, message = "Part order must be at least 1")
    private Integer partOrder;

    @JsonProperty("question_ordering_id")
    private String questionOrderingId;
}
