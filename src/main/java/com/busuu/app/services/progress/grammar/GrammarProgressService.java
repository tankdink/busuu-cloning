package com.busuu.app.services.progress.grammar;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.entities.Grammar;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.GrammarProgress;
import com.busuu.app.entities.progresses.GrammarSectionProgress;
import com.busuu.app.repositories.progress.GrammarProgressRepository;
import com.busuu.app.repositories.progress.GrammarSectionProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrammarProgressService implements IGrammarProgressService {

    private final GrammarSectionProgressRepository grammarSectionProgressRepository;
    private final GrammarProgressRepository grammarProgressRepository;

    @Override
    @Transactional
    public GrammarProgress upsertGrammarProgress(Grammar grammar, User user) {
        List<GrammarSection> sections = grammar.getGrammarSections();
        if (sections.isEmpty()) return null;

        double totalProgress = 0.0;

        for (GrammarSection section : sections) {
            GrammarSectionProgress sectionProgress = grammarSectionProgressRepository
                    .findByGrammarSectionIdAndUserId(section.getId(), user.getId());
            totalProgress += (sectionProgress != null) ? sectionProgress.getProgress() : 0;
        }

        double avgProgress = totalProgress / sections.size();
        boolean isCompleted = avgProgress >= Constants.PASSING_PROGRESS;

        GrammarProgress grammarProgress = grammarProgressRepository.findByGrammarIdAndUserId(grammar.getId(), user.getId());
        if (grammarProgress == null) {
            grammarProgress = GrammarProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .grammar(grammar)
                    .user(user)
                    .progress(avgProgress)
                    .isCompleted(isCompleted)
                    .build();
        } else {
            grammarProgress.setProgress(avgProgress);
            grammarProgress.setIsCompleted(isCompleted);
        }

        return grammarProgressRepository.save(grammarProgress);
    }

}
