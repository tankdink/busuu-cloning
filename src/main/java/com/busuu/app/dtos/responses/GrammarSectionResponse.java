package com.busuu.app.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GrammarSectionResponse extends BaseResponse
{

    @JsonProperty("id")
    private String id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("content")
    private String content;

    @JsonProperty("grammar_section_order")
    private Integer grammarSectionOrder;

    @JsonProperty("grammar_id")
    private String grammarId;

    @JsonProperty("lesson_id")
    private String lessonId;

    @JsonProperty("level_id")
    private String levelId;
}
