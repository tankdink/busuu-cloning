package com.busuu.app.dtos.requests.word;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordExampleDTO {

    @JsonProperty("word_id")
    @NotBlank(message = "Word ID cannot be null or empty.")
    private String wordId;

    @JsonProperty("original_text")
    @NotBlank(message = "original_text")
    private String originalText;

    @JsonProperty("translate_text")
    private String translateText;

    @JsonProperty("audio")
    private MultipartFile audio;
}
