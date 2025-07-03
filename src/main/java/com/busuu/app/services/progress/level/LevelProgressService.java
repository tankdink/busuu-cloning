package com.busuu.app.services.progress.level;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Level;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.ChapterProgress;
import com.busuu.app.entities.progresses.LevelProgress;
import com.busuu.app.repositories.progress.ChapterProgressRepository;
import com.busuu.app.repositories.progress.LevelProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LevelProgressService implements ILevelProgressService {

    private final LevelProgressRepository levelProgressRepository;
    private final ChapterProgressRepository chapterProgressRepository;

    @Override
    @Transactional
    public LevelProgress upsertLevelProgress(Level level, User user) {
        List<Chapter> chapters = level.getChapters();
        if (chapters.isEmpty()) return null;

        List<ChapterProgress> progresses = chapterProgressRepository.findByUserIdAndChapterIdIn(
                user.getId(),
                chapters.stream().map(Chapter::getId).toList()
        );

        double avgProgress = progresses.isEmpty() ? 0 : progresses.stream().mapToDouble(ChapterProgress::getProgress).average().orElse(0);
        boolean isCompleted = avgProgress >= Constants.PASSING_PROGRESS;

        LevelProgress levelProgress = levelProgressRepository.findByLevelIdAndUserId(level.getId(), user.getId());
        if (levelProgress == null) {
            levelProgress = LevelProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .level(level)
                    .user(user)
                    .progress(avgProgress)
                    .isCompleted(isCompleted)
                    .build();
        } else {
            levelProgress.setProgress(avgProgress);
            levelProgress.setIsCompleted(isCompleted);
        }

        return levelProgressRepository.save(levelProgress);
    }

}
