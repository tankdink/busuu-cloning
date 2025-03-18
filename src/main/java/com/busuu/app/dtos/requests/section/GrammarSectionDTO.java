package com.busuu.app.dtos.requests.section;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrammarSectionDTO
{
    @NotBlank(message = "Grammar Section Title is required!")
    @Size(min = 1, max = 255, message = "Invalid Title length (minimum length is 1, maximum length is 255)!")
    private String title;

    @NotBlank(message = "Grammar Section Description is required!")
    @Size(min = 1, message = "Invalid Description length (minimum length is 1)!")
    private String description;

    @NotBlank(message = "Grammar Section Content is required!")
    @Size(min = 1, message = "Invalid Content length (minimum length is 1)!")
    private String content;

    @NotNull(message = "Grammar Section Order is required!")
    @Min(value = 1, message = "The Grammar Section Order must be at least 1")
    @JsonProperty("grammar_section_order")
    private Integer grammarSectionOrder;

    @NotBlank(message = "The Grammar ID to which this grammar section belongs is required!")
    @JsonProperty("grammar_id")
    private String grammarId;

    @NotBlank(message = "The Lesson ID to which this grammar section belongs is required")
    @JsonProperty("lesson_id")
    private String lessonId;
}
