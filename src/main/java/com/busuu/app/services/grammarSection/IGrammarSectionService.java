package com.busuu.app.services.grammarSection;

import com.busuu.app.dtos.requests.section.GrammarSectionDTO;
import com.busuu.app.dtos.responses.GrammarSectionResponse;

import java.util.List;

public interface IGrammarSectionService
{
    GrammarSectionResponse insertGrammarSection(String requestId, GrammarSectionDTO grammarSectionDTO);
    List<GrammarSectionResponse> getGrammarSections(String requestId);
    GrammarSectionResponse getGrammarSection(String requestId, String grammarSectionID);
    List<GrammarSectionResponse> getByGrammarId(String requestId, String grammarId);
    GrammarSectionResponse updateGrammarSection(String requestId, String grammarSectionID, GrammarSectionDTO infoUpdateGrammarSection);
    void deleteGrammarSection(String requestId, String grammarSectionID);
}
