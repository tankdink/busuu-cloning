package com.busuu.app.services.question.multiple_choice;

import com.busuu.app.dtos.requests.questions.QuestionDTO;
import com.busuu.app.dtos.requests.questions.multiple_choice.QuestionMultipleChoiceDTO;
import com.busuu.app.dtos.responses.question.multiple_choice.QuestionMultipleChoiceResponse;

import java.util.List;

public interface IQuestionMultipleChoiceService {

    QuestionMultipleChoiceResponse insertQuestion (String requestId, QuestionMultipleChoiceDTO questionDTO);

    QuestionMultipleChoiceResponse getQuestion (String requestId, String questionId);

    QuestionMultipleChoiceResponse updateQuestion (String requestId, String questionId, QuestionDTO questionDTO);

    void deleteQuestion (String requestId, String questionId);
}
