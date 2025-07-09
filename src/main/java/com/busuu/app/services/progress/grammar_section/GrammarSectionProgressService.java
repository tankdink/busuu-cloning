package com.busuu.app.services.progress.grammar_section;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.entities.GrammarSection;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.GrammarSectionProgress;
import com.busuu.app.repositories.progress.GrammarSectionProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GrammarSectionProgressService implements IGrammarSectionProgressService {

    private final GrammarSectionProgressRepository grammarSectionProgressRepository;

    @Override
    @Transactional
    public GrammarSectionProgress upsertGrammarSectionProgress(GrammarSection section, User user, int numCorrectQuestions) {
        int total = section.getQuestions().size();
        double progress = total == 0 ? 0 : (double) numCorrectQuestions / total * 100;

        GrammarSectionProgress sectionProgress = grammarSectionProgressRepository
                .findByGrammarSectionIdAndUserId(section.getId(), user.getId());

        if (sectionProgress == null) {
            sectionProgress = GrammarSectionProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .grammarSection(section)
                    .user(user)
                    .progress(progress)
                    .isCompleted(progress >= Constants.PASSING_PROGRESS)
                    .build();
        } else {
            double maxProgress = Math.max(progress, sectionProgress.getProgress());
            sectionProgress.setProgress(maxProgress);
            sectionProgress.setIsCompleted(maxProgress >= Constants.PASSING_PROGRESS);
        }

        return grammarSectionProgressRepository.save(sectionProgress);
    }
}
