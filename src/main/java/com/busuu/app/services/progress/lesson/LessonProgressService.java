package com.busuu.app.services.progress.lesson;

import com.busuu.app.configs.constant.Constants;
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
        int totalQuestions = lesson.getQuestions().size();
        double progress = totalQuestions == 0 ? 0 : (double) numCorrectQuestions / totalQuestions * 100;

        LessonProgress lessonProgress = lessonProgressRepository.findByLessonIdAndUserId(lesson.getId(), user.getId());
        if (lessonProgress == null) {
            lessonProgress = LessonProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .lesson(lesson)
                    .user(user)
                    .progress(progress)
                    .isCompleted(progress >= Constants.PASSING_PROGRESS)
                    .build();
        } else {
            double maxProgress = Math.max(progress, lessonProgress.getProgress());
            lessonProgress.setProgress(maxProgress);
            lessonProgress.setIsCompleted(maxProgress >= Constants.PASSING_PROGRESS);
        }

        return lessonProgressRepository.save(lessonProgress);
    }
}
