package com.busuu.app.services.question.ordering;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.questions.ordering.OrderingPartDTO;
import com.busuu.app.dtos.responses.question.ordering.OrderingPartResponse;
import com.busuu.app.entities.questions.ordering.OrderingPart;
import com.busuu.app.entities.questions.ordering.QuestionOrdering;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.questions.ordering.OrderingPartRepository;
import com.busuu.app.repositories.questions.ordering.QuestionOrderingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderingPartService implements IOrderingPartService {
    private final OrderingPartRepository orderingPartRepository;
    private final QuestionOrderingRepository questionOrderingRepository;
    private final ModelMapper modelMapper;
    @Override
    @Transactional
    public OrderingPartResponse insertOrderingPart(String requestId, OrderingPartDTO orderingPartDTO) {
        try {
            QuestionOrdering existingQuestion = questionOrderingRepository.findById(orderingPartDTO.getQuestionOrderingId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot not find Question with ID = " + orderingPartDTO.getQuestionOrderingId()));

            OrderingPart orderingPart = modelMapper.map(orderingPartDTO, OrderingPart.class);
            orderingPart.setQuestionOrdering(existingQuestion);

            orderingPart = orderingPartRepository.save(orderingPart);

            OrderingPartResponse response = modelMapper.map(orderingPart, OrderingPartResponse.class);
            response.setQuestionId(orderingPart.getQuestionOrdering().getId());

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create ordering part, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_ORDERING_PART, requestId);
        }
    }

    @Override
    public OrderingPartResponse getOrderingPart(String requestId, String orderingPartId) {
        try {
            OrderingPart existingOrderingPart = orderingPartRepository.findById(orderingPartId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Ordering Part with ID = " + orderingPartId));

            OrderingPartResponse response = modelMapper.map(existingOrderingPart, OrderingPartResponse.class);
            response.setQuestionId(existingOrderingPart.getQuestionOrdering().getId());

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to get ordering part, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_ORDERING_PART, requestId);
        }
    }

    @Override
    public List<OrderingPartResponse> getByQuestion(String requestId, String questionId) {
        try {
            QuestionOrdering existingQuestionOrdering = questionOrderingRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));
            List<OrderingPart> existingOrderingParts = orderingPartRepository.findByQuestionOrdering(existingQuestionOrdering);

            return existingOrderingParts.stream().map(
                    orderingPart -> {
                        OrderingPartResponse response = modelMapper.map(orderingPart, OrderingPartResponse.class);
                        response.setQuestionId(orderingPart.getQuestionOrdering().getId());
                        return response;
                    }
            ).toList();
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to get ordering part, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_ORDERING_PART, requestId);
        }
    }

    @Override
    public OrderingPartResponse updateOrderingPart(String requestId, String orderingPartId, OrderingPartDTO orderingPartDTO) {
        try {
            OrderingPart existingOrderingPart = orderingPartRepository.findById(orderingPartId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Ordering Part with ID = " + orderingPartId));

            if (!existingOrderingPart.getQuestionOrdering().getId().equals(orderingPartDTO.getQuestionOrderingId())) {
                QuestionOrdering existingQuestion = questionOrderingRepository.findById(orderingPartDTO.getQuestionOrderingId())
                        .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + orderingPartDTO.getQuestionOrderingId()));
                existingOrderingPart.setQuestionOrdering(existingQuestion);
            }
            modelMapper.map(orderingPartDTO, existingOrderingPart);

            existingOrderingPart = orderingPartRepository.save(existingOrderingPart);

            OrderingPartResponse response = modelMapper.map(existingOrderingPart, OrderingPartResponse.class);
            response.setQuestionId(existingOrderingPart.getQuestionOrdering().getId());

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to update ordering part, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_ORDERING_PART, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteOrderingPart(String requestId, String orderingPartId) {
        try {
            orderingPartRepository.deleteById(orderingPartId);
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to delete ordering part, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_ORDERING_PART, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteByQuestionId(String requestId, String questionId) {
        try {
            QuestionOrdering existingQuestionOrdering = questionOrderingRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));
            orderingPartRepository.deleteByQuestionOrdering(existingQuestionOrdering);
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to delete ordering part, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_ORDERING_PART, requestId);
        }
    }
}
