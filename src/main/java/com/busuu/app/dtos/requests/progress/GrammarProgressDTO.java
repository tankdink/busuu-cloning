package com.busuu.app.dtos.requests.progress;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrammarProgressDTO {

    @JsonProperty("user_id")
    @NotBlank(message = "User's ID is required")
    private String userId;

    @JsonProperty("grammar_id")
    @NotBlank(message = "Grammar's ID is required")
    private String grammarId;

    @JsonProperty("grammar_section_id")
    @NotBlank(message = "Grammar Section's ID is required")
    private String grammarSectionId;
}
