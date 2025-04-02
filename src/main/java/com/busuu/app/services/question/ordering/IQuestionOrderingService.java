package com.busuu.app.services.question.ordering;

import com.busuu.app.dtos.requests.questions.QuestionDTO;
import com.busuu.app.dtos.requests.questions.QuestionFillBlankDTO;
import com.busuu.app.dtos.responses.question.ordering.QuestionOrderingResponse;

import java.util.List;

public interface IQuestionOrderingService {

    QuestionOrderingResponse insertQuestion (String requestId, QuestionFillBlankDTO questionDTO);

    QuestionOrderingResponse getQuestion (String requestId, String questionId);

    QuestionOrderingResponse updateQuestion (String requestId, String questionId, QuestionFillBlankDTO questionDTO);

    void deleteQuestion (String requestId, String questionId);
}
