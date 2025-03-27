package com.busuu.app.services.question.fill_blank;

import com.busuu.app.dtos.requests.questions.QuestionFillBlankDTO;
import com.busuu.app.dtos.responses.question.QuestionFillBlankResponse;

import java.util.List;

public interface IQuestionFillBlankService {

    QuestionFillBlankResponse insertQuestion (String requestId, QuestionFillBlankDTO questionFillBlankDTO);

    QuestionFillBlankResponse getQuestion (String requestId, String questionId);

    QuestionFillBlankResponse updateQuestion (String requestId, String questionId, QuestionFillBlankDTO questionFillBlankDTO);

    void deleteQuestion (String requestId, String questionId);
}
