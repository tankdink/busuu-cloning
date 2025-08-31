package com.busuu.app.dtos.requests.word;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordDTO {

    @JsonProperty("language_code")
    private String languageCode;

    @NotBlank(message = "Text cannot be null or empty.")
    @JsonProperty("text")
    private String text;

    @JsonProperty("translation")
    private String translation;

    @JsonProperty("audio_vocab")
    private MultipartFile audioVocab;

    @JsonProperty("image")
    private MultipartFile image;

    @JsonProperty("lesson_id")
    @NotBlank(message = "Lesson ID cannot be null or empty.")
    private String lessonId;

    // Word example
    @JsonProperty("original_text")
    @NotBlank(message = "original_text")
    private String originalText;

    @JsonProperty("translate_text")
    private String translateText;

    @JsonProperty("audio")
    private MultipartFile audio;

}
