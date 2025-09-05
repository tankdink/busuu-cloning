package com.busuu.app.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrammarResponse extends BaseResponse
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

    @JsonProperty("progress")
    private Double progress;

    @JsonProperty("is_completed")
    private Boolean isCompleted;
}
