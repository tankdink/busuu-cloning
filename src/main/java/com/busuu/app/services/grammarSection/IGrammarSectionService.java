package com.busuu.app.services.grammarSection;

import com.busuu.app.dtos.requests.section.GrammarSectionDTO;
import com.busuu.app.dtos.responses.GrammarSectionResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IGrammarSectionService
{
    GrammarSectionResponse insertGrammarSection(String requestId, GrammarSectionDTO grammarSectionDTO);
    Page<GrammarSectionResponse> getGrammarSections(String requestId, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, List<String> filterBy, List<String> filterValue);
    GrammarSectionResponse getGrammarSection(String requestId, String grammarSectionID);
    List<GrammarSectionResponse> getByGrammarId(String requestId, String grammarId);
    GrammarSectionResponse updateGrammarSection(String requestId, String grammarSectionID, GrammarSectionDTO infoUpdateGrammarSection);
    void deleteGrammarSection(String requestId, String grammarSectionID);
}
