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

        double totalProgress = 0.0;

        for (Chapter chapter : chapters) {
            ChapterProgress progress = chapterProgressRepository.findByChapterIdAndUserId(chapter.getId(), user.getId());
            totalProgress += (progress != null) ? progress.getProgress() : 0;
        }

        double avgProgress = totalProgress / chapters.size();
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
