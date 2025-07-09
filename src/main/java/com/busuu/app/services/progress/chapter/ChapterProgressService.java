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

        double totalProgress = 0.0;

        for (Lesson lesson : lessons) {
            LessonProgress lessonProgress = lessonProgressRepository.findByLessonIdAndUserId(lesson.getId(), user.getId());
            totalProgress += (lessonProgress != null) ? lessonProgress.getProgress() : 0;
        }

        double avgProgress = totalProgress / lessons.size();
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
