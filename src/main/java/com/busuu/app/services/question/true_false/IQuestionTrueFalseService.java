package com.busuu.app.services.question.true_false;

import com.busuu.app.dtos.requests.questions.QuestionTrueFalseDTO;
import com.busuu.app.dtos.responses.question.QuestionTrueFalseResponse;

import java.util.List;

public interface IQuestionTrueFalseService {

    QuestionTrueFalseResponse insertQuestion (String requestId, QuestionTrueFalseDTO questionTrueFalseDTO);

    QuestionTrueFalseResponse getQuestion (String requestId, String questionId);

    QuestionTrueFalseResponse updateQuestion (String requestId, String questionId, QuestionTrueFalseDTO questionTrueFalseDTO);

    void deleteQuestion (String requestId, String questionId);
}
