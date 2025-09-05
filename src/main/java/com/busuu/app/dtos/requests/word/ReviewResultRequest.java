package com.busuu.app.dtos.requests.word;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewResultRequest {
    @JsonProperty("word_id")
    @NotBlank(message = "Word ID cannot be null or empty")
    private String wordId;

    @JsonProperty("is_correct")
    @NotNull(message = "Is Correct cannot be null or empty")
    private Boolean isCorrect;
}
