package com.busuu.app.services.progress.grammar_section;

import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.GrammarSectionProgress;
import com.busuu.app.repositories.progress.GrammarSectionProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrammarSectionProgressService implements IGrammarSectionProgressService {

    private final GrammarSectionProgressRepository grammarSectionProgressRepository;

    @Override
    public GrammarSectionProgress upsertGrammarSectionProgress(GrammarSection section, User user, int numCorrectQuestions) {
        double progress = ((double) numCorrectQuestions / section.getQuestions().size()) * 100;

        GrammarSectionProgress sectionProgress = grammarSectionProgressRepository
                .findByGrammarSectionIdAndUserId(section.getId(), user.getId());

        if (sectionProgress == null) {
            sectionProgress = GrammarSectionProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .grammarSection(section)
                    .user(user)
                    .progress(progress)
                    .isCompleted(progress >= 80)
                    .build();
        } else {
            double maxProgress = Math.max(progress, sectionProgress.getProgress());
            sectionProgress.setProgress(maxProgress);
            sectionProgress.setIsCompleted(maxProgress >= 80);
        }

        return grammarSectionProgressRepository.save(sectionProgress);
    }
}
