package com.busuu.app.services.progress.lesson;

import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.LessonProgress;
import com.busuu.app.repositories.progress.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonProgressService implements ILessonProgressService {

    private final LessonProgressRepository lessonProgressRepository;

    @Override
    @Transactional
    public LessonProgress upsertLessonProgress(Lesson lesson, User user, int numCorrectQuestions) {
        double progress = ((double) numCorrectQuestions / lesson.getQuestions().size()) * 100;

        LessonProgress lessonProgress = lessonProgressRepository.findByLessonIdAndUserId(lesson.getId(), user.getId());
        if (lessonProgress == null) {
            lessonProgress = LessonProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .lesson(lesson)
                    .user(user)
                    .progress(progress)
                    .isCompleted(progress >= 80)
                    .build();
        } else {
            double maxProgress = Math.max(progress, lessonProgress.getProgress());
            lessonProgress.setProgress(maxProgress);
            lessonProgress.setIsCompleted(maxProgress >= 80);
        }

        return lessonProgressRepository.save(lessonProgress);
    }
}
