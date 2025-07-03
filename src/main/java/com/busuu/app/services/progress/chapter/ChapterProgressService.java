package com.busuu.app.services.progress.chapter;

import com.busuu.app.configs.constant.Constants;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChapterProgressService implements IChapterProgressService{

    private final LessonProgressRepository lessonProgressRepository;
    private final ChapterProgressRepository chapterProgressRepository;

    @Override
    @Transactional
    public ChapterProgress upsertChapterProgress(Chapter chapter, User user) {
        List<Lesson> lessons = chapter.getLessons();
        if (lessons.isEmpty()) return null;

        List<LessonProgress> progresses = lessonProgressRepository.findByUserIdAndLessonIdIn(
                user.getId(),
                lessons.stream().map(Lesson::getId).toList()
        );

        double avgProgress = progresses.isEmpty() ? 0 : progresses.stream().mapToDouble(LessonProgress::getProgress).average().orElse(0);
        boolean isCompleted = avgProgress >= Constants.PASSING_PROGRESS;

        ChapterProgress chapterProgress = chapterProgressRepository.findByChapterIdAndUserId(chapter.getId(), user.getId());
        if (chapterProgress == null) {
            chapterProgress = ChapterProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .chapter(chapter)
                    .user(user)
                    .progress(avgProgress)
                    .isCompleted(isCompleted)
                    .build();
        } else {
            chapterProgress.setProgress(avgProgress);
            chapterProgress.setIsCompleted(isCompleted);
        }

        return chapterProgressRepository.save(chapterProgress);
    }
}
