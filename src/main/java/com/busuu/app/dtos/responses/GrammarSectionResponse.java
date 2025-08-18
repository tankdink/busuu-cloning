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

    @JsonProperty("progress")
    private Double progress;

    @JsonProperty("is_completed")
    private Boolean isCompleted;
}
