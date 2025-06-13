package com.busuu.app.services.question;

import com.busuu.app.dtos.requests.questions.QuestionDTO;
import com.busuu.app.dtos.responses.question.QuestionResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IQuestionService {

    Page<QuestionResponse> getByLessonId (String requestId, String lessonId, int page, int size, String sortBy, String sortDirection);

    Page<QuestionResponse> getByGrammarSectionId (String requestId, String grammarSectionId, int page, int size, String sortBy, String sortDirection);

    QuestionResponse getById(String requestId, String questionId);

    Page<QuestionResponse> getByQuestionType (String requestId, String questionType, int page, int size, String sortBy, String sortDirection);

    void deleteByLessonId (String requestId, String lessonId);

    void deleteByGrammarSectionId (String requestId, String grammarSectionId);
}
