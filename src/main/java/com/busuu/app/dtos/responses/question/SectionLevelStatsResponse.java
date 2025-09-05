package com.busuu.app.dtos.responses.question;

import com.busuu.app.dtos.responses.GrammarSectionResponse;
import com.busuu.app.entities.SectionLevel;
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
public class SectionLevelStatsResponse {

    @JsonProperty("level")
    private SectionLevel level;

    @JsonProperty("total")
    private long total;

    @JsonProperty("grammar_section")
    private List<GrammarSectionResponse> grammarSection;
}
