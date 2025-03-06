package com.busuu.app.services.lesson;

import com.busuu.app.dtos.requests.lesson.LessonDTO;
import com.busuu.app.dtos.responses.LessonResponse;

import java.util.List;

public interface ILessonService {

    LessonResponse insertLesson (String requestId, LessonDTO lessonDTO);

    LessonResponse getLesson (String requestId, String lessonId);

    List<LessonResponse> getLessons (String requestId);

    LessonResponse updateLesson (String requestId, String lessonId, LessonDTO lessonDTO);

    void deleteLesson (String requestId, String lessonId);
}
