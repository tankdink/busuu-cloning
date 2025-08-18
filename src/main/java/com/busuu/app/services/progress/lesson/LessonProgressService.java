package com.busuu.app.services.progress.lesson;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.User;
import com.busuu.app.entities.UserWord;
import com.busuu.app.entities.Word;
import com.busuu.app.entities.progresses.LessonProgress;
import com.busuu.app.repositories.UserWordRepository;
import com.busuu.app.repositories.progress.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonProgressService implements ILessonProgressService {

    private final LessonProgressRepository lessonProgressRepository;
    private final UserWordRepository userWordRepository;

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

        lessonProgress = lessonProgressRepository.save(lessonProgress);

        if (lessonProgress.getIsCompleted()) {
            lesson.getWords().forEach(word -> {
                Optional<UserWord> optionalUserWord = userWordRepository.findByUserIdAndWordId(user.getId(), word.getId());

                if (optionalUserWord.isPresent()) {
                    UserWord userWord = optionalUserWord.get();
                    if (Boolean.FALSE.equals(userWord.getIsActive())) {
                        userWord.setIsActive(true);
                        userWordRepository.save(userWord);
                    }
                } else {
                    UserWord userWord = UserWord.builder()
                            .id(UUID.randomUUID().toString())
                            .isActive(true)
                            .word(word)
                            .user(user)
//                            .nextReviewDate(LocalDateTime.now().plusHours(12))
                            .nextReviewDate(LocalDateTime.now())
                            .build();
                    userWordRepository.save(userWord);
                }
            });
        }
        return lessonProgress;
    }
}
