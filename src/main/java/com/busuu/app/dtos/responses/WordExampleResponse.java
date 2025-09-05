package com.busuu.app.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class WordExampleResponse extends BaseResponse{

    @JsonProperty("id")
    private String id;

    @JsonProperty("word_id")
    private String wordId;

    @JsonProperty("original_text")
    private String originalText;

    @JsonProperty("translate_text")
    private String translateText;

    @JsonProperty("audio_url")
    private String audioUrl;

    @JsonProperty("audio_name")
    private String audioName;
}
