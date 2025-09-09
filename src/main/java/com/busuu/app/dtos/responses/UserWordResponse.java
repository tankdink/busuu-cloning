package com.busuu.app.dtos.responses;

import com.busuu.app.entities.enums.StrengthLevel;
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
public class UserWordResponse extends BaseResponse {

    @JsonProperty("strength_level")
    private StrengthLevel strengthLevel;

    @JsonProperty("is_favorite")
    private Boolean isFavorite;

    private String id;

    private WordResponse word;
}
