package com.busuu.app.services.progress.chapter;

import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.ChapterProgress;
import com.busuu.app.entities.progresses.LessonProgress;
import com.busuu.app.repositories.progress.ChapterProgressRepository;
import com.busuu.app.repositories.progress.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChapterProgressService implements IChapterProgressService{

    private final LessonProgressRepository lessonProgressRepository;
    private final ChapterProgressRepository chapterProgressRepository;

    @Override
    @Transactional
    public ChapterProgress upsertChapterProgress (Chapter chapter, User user) {
        List<Lesson> lessons = chapter.getLessons();
        long completedLessons = lessons.stream()
                .map(lesson -> lessonProgressRepository.findByLessonIdAndUserId(lesson.getId(), user.getId()))
                .filter(Objects::nonNull)
                .filter(LessonProgress::getIsCompleted)
                .count();

        double progress = (double) completedLessons / lessons.size() * 100;

        ChapterProgress chapterProgress = chapterProgressRepository.findByChapterIdAndUserId(chapter.getId(), user.getId());
        if (chapterProgress == null) {
            chapterProgress = ChapterProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .chapter(chapter)
                    .user(user)
                    .progress(progress)
                    .isCompleted(progress >= 80)
                    .build();
        } else {
            chapterProgress.setProgress(progress);
            chapterProgress.setIsCompleted(progress >= 80);
        }

        return chapterProgressRepository.save(chapterProgress);
    }
}
