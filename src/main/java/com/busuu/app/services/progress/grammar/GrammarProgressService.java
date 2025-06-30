package com.busuu.app.services.progress.grammar;

import com.busuu.app.entities.Grammar;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.GrammarProgress;
import com.busuu.app.entities.progresses.GrammarSectionProgress;
import com.busuu.app.repositories.progress.GrammarProgressRepository;
import com.busuu.app.repositories.progress.GrammarSectionProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrammarProgressService implements IGrammarProgressService {

    private final GrammarSectionProgressRepository grammarSectionProgressRepository;
    private final GrammarProgressRepository grammarProgressRepository;

    @Override
    public GrammarProgress upsertGrammarProgress(Grammar grammar, User user) {
        List<GrammarSection> sections = grammar.getGrammarSections();
        long completedSections = sections.stream()
                .map(section -> grammarSectionProgressRepository.findByGrammarSectionIdAndUserId(section.getId(), user.getId()))
                .filter(Objects::nonNull)
                .filter(GrammarSectionProgress::getIsCompleted)
                .count();

        double progress = (double) completedSections / sections.size() * 100;

        GrammarProgress grammarProgress = grammarProgressRepository.findByGrammarIdAndUserId(grammar.getId(), user.getId());
        if (grammarProgress == null) {
            grammarProgress = GrammarProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .grammar(grammar)
                    .user(user)
                    .progress(progress)
                    .isCompleted(progress >= 80)
                    .build();
        } else {
            grammarProgress.setProgress(progress);
            grammarProgress.setIsCompleted(progress >= 80);
        }

        return grammarProgressRepository.save(grammarProgress);
    }
}
