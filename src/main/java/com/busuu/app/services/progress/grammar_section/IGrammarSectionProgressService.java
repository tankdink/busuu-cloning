package com.busuu.app.services.progress.grammar_section;

import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.GrammarSectionProgress;

public interface IGrammarSectionProgressService {

    GrammarSectionProgress upsertGrammarSectionProgress(GrammarSection section, User user, int numCorrectQuestions);
}
