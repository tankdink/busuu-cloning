package com.busuu.app.services.lesson;

import com.busuu.app.dtos.requests.lesson.LessonDTO;
import com.busuu.app.dtos.responses.LessonResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ILessonService {

    LessonResponse insertLesson (String requestId, LessonDTO lessonDTO);

    LessonResponse getLesson (String requestId, String lessonId);

    Page<LessonResponse> getLessons (String requestId, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, List<String> filterBy, List<String> filterValue);

    LessonResponse updateLesson (String requestId, String lessonId, LessonDTO lessonDTO);

    void deleteLesson (String requestId, String lessonId);

    List<LessonResponse> getByChapterId (String requestId, String chapterId);
}
