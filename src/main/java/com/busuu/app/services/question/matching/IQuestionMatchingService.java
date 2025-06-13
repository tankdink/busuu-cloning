package com.busuu.app.services.question.matching;

import com.busuu.app.dtos.requests.questions.QuestionDTO;
import com.busuu.app.dtos.requests.questions.matching.QuestionMatchingDTO;
import com.busuu.app.dtos.responses.question.matching.QuestionMatchingResponse;

import java.util.List;

public interface IQuestionMatchingService {
    QuestionMatchingResponse insertQuestion (String requestId, QuestionMatchingDTO questionDTO);

    QuestionMatchingResponse getQuestion (String requestId, String questionId);


    QuestionMatchingResponse updateQuestion (String requestId, String questionId, QuestionDTO questionDTO);

    void deleteQuestion (String requestId, String questionId);
}
