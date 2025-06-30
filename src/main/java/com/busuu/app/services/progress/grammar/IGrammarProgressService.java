package com.busuu.app.services.progress.grammar;

import com.busuu.app.entities.Grammar;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.GrammarProgress;

public interface IGrammarProgressService {

    GrammarProgress upsertGrammarProgress(Grammar grammar, User user);
}
