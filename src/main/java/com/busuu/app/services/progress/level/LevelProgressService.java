package com.busuu.app.services.progress.level;

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
        long completedChapters = chapters.stream()
                .map(ch -> chapterProgressRepository.findByChapterIdAndUserId(ch.getId(), user.getId()))
                .filter(Objects::nonNull)
                .filter(ChapterProgress::getIsCompleted)
                .count();

        double progress = (double) completedChapters / chapters.size() * 100;

        LevelProgress levelProgress = levelProgressRepository.findByLevelIdAndUserId(level.getId(), user.getId());
        if (levelProgress == null) {
            levelProgress = LevelProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .level(level)
                    .user(user)
                    .progress(progress)
                    .isCompleted(progress >= 80)
                    .build();
        } else {
            levelProgress.setProgress(progress);
            levelProgress.setIsCompleted(progress >= 80);
        }

        return levelProgressRepository.save(levelProgress);
    }
}
