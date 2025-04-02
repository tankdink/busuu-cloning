package com.busuu.app.services.question.ordering;

import com.busuu.app.dtos.requests.questions.multiple_choice.MultipleChoiceOptionDTO;
import com.busuu.app.dtos.requests.questions.ordering.OrderingPartDTO;
import com.busuu.app.dtos.responses.question.multiple_choice.MultipleChoiceOptionResponse;
import com.busuu.app.dtos.responses.question.ordering.OrderingPartResponse;

import java.util.List;

public interface IOrderingPartService {
    OrderingPartResponse insertOrderingPart (String requestId, OrderingPartDTO orderingPartDTO);

    OrderingPartResponse getOrderingPart (String requestId, String orderingPartId);

    List<OrderingPartResponse> getByQuestion (String requestId, String questionId);

    OrderingPartResponse updateOrderingPart (String requestId, String orderingPartId, OrderingPartDTO orderingPartDTO);

    void deleteOrderingPart (String requestId, String orderingPartId);

    void deleteByQuestionId (String requestId, String questionId);
}
