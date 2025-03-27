package com.busuu.app.services.question.multiple_choice;

import com.busuu.app.dtos.requests.questions.multiple_choice.MultipleChoiceOptionDTO;
import com.busuu.app.dtos.responses.question.multiple_choice.MultipleChoiceOptionResponse;

import java.util.List;

public interface IMultipleChoiceOptionService {

    MultipleChoiceOptionResponse insertMultipleChoiceOption (String requestId, MultipleChoiceOptionDTO multipleChoiceOptionDTO);

    MultipleChoiceOptionResponse getMultipleChoiceOption (String requestId, String multipleChoiceId);

    List<MultipleChoiceOptionResponse> getByQuestion (String requestId, String questionId);

    MultipleChoiceOptionResponse updateMultipleChoiceOption (String requestId, String multipleChoiceId, MultipleChoiceOptionDTO multipleChoiceOptionDTO);

    void deleteMultipleChoiceOption (String requestId, String multipleChoiceId);

    void deleteByQuestionId (String requestId, String questionId);
}
