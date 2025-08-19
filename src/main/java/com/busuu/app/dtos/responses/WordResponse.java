package com.busuu.app.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordResponse extends BaseResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("language_code")
    private String languageCode;

    @JsonProperty("text")
    private String text;

    @JsonProperty("translation")
    private String translation;

    @JsonProperty("audio_url")
    private String audioUrl;

    @JsonProperty("audio_name")
    private String audioName;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("image_name")
    private String imageName;

    @JsonProperty("lesson_id")
    private String lessonId;

    @JsonProperty("examples")
    private List<WordExampleResponse> examples;
}
