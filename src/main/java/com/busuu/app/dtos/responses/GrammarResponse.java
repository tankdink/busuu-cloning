package com.busuu.app.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrammarResponse
{

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("flag_icon_url")
    private String flagIconUrl;

    @JsonProperty("flag_icon_name")
    private String flagIconName;

    @JsonProperty("grammar_order")
    private Integer grammarOrder;

    @JsonProperty("language_id")
    private String languageId;
}
