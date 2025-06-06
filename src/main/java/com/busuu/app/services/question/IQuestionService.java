package com.busuu.app.services.question;

import com.busuu.app.dtos.responses.question.QuestionResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IQuestionService {

    List<QuestionResponse> getByLessonId (String requestId, String lessonId);

    List<QuestionResponse> getByGrammarSectionId (String requestId, String grammarSectionId);

    Page<QuestionResponse> getByQuestiontype (String requestId, String questionType, int page, int size, String sortBy, String sortDirection);

    void deleteByLessonId (String requestId, String lessonId);

    void deleteByGrammarSectionId (String requestId, String grammarSectionId);
}
