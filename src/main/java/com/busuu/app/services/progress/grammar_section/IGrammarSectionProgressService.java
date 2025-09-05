package com.busuu.app.services.progress.grammar_section;

import com.busuu.app.dtos.responses.question.SectionLevelStatsResponse;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.GrammarSectionProgress;

import java.util.List;

public interface IGrammarSectionProgressService {

    GrammarSectionProgress upsertGrammarSectionProgress(GrammarSection section, User user, int numCorrectQuestions);

    long countSectionProgress (String requestId);

    List<SectionLevelStatsResponse> sectionStats(String requestId);
}
