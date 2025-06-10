package com.busuu.app.services.grammar;

import com.busuu.app.dtos.requests.grammar.GrammarDTO;
import com.busuu.app.dtos.responses.GrammarResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IGrammarService
{

    GrammarResponse insertGrammar(String requestId, GrammarDTO grammarDTO);
    Page<GrammarResponse> getGrammars(String requestId, int page, int size, String sortBy, String sortDirection);
    GrammarResponse getGrammar(String requestId, String grammarID);
    Page<GrammarResponse> getByLanguageId(String requestId, String languageID, int page, int size, String sortBy, String sortDirection );
    GrammarResponse updateGrammar(String requestId, String grammarID, GrammarDTO infoUpdateGrammar);
    void deleteGrammar(String requestId, String grammarID);
    
}
