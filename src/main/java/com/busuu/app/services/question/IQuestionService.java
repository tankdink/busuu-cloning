package com.busuu.app.services.question;

import com.busuu.app.dtos.responses.question.QuestionResponse;

import java.util.List;

public interface IQuestionService {

    List<QuestionResponse> getByLessonId (String requestId, String lessonId);

    List<QuestionResponse> getByGrammarSectionId (String requestId, String grammarSectionId);

    void deleteByLessonId (String requestId, String lessonId);

    void deleteByGrammarSectionId (String requestId, String grammarSectionId);
}
