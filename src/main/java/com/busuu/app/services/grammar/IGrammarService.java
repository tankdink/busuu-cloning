package com.busuu.app.services.grammar;

import com.busuu.app.dtos.requests.grammar.GrammarDTO;
import com.busuu.app.dtos.responses.GrammarResponse;

import java.util.List;

public interface IGrammarService
{

    GrammarResponse insertGrammar(String requestId, GrammarDTO grammarDTO);
    List<GrammarResponse> getGrammars(String requestId);
    GrammarResponse getGrammar(String requestId, String grammarID);
    List<GrammarResponse> getByLanguageId(String requestId, String languageID);
    GrammarResponse updateGrammar(String requestId, String grammarID, GrammarDTO infoUpdateGrammar);
    void deleteGrammar(String requestId, String grammarID);
    
}
