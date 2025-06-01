package com.busuu.app.dtos.requests.grammar;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrammarDTO 
{
    @NotBlank(message = "Grammar Title is required!")
    @Size(min = 1, max = 255, message = "Invalid Title length (minimum length is 1, maximum length is 255)!")
    private String title;

    @NotBlank(message = "Grammar Description is required!")
    @Size(min = 1, message = "Invalid Description length (minimum length is 1)!")
    private String description;

    @JsonProperty("flag_icon")
    private MultipartFile flagIcon;

//    @NotNull(message = "Grammar Order is required!")
//    @Min(value = 1, message = "The Grammar Order must be at least 1")
    @JsonProperty("grammar_order")
    private Integer grammarOrder;

    @NotBlank(message = "The Language ID to which this grammar belongs is required")
    @Size(min = 1, message = "Invalid Language ID length (minimum length is 1)!")
    @JsonProperty("language_id")
    private String languageId;

}
