package com.busuu.app.services.progress.lesson;

import com.busuu.app.entities.Lesson;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.LessonProgress;

public interface ILessonProgressService {

    LessonProgress upsertLessonProgress (Lesson lesson, User user, int numCorrectQuestions);
}
