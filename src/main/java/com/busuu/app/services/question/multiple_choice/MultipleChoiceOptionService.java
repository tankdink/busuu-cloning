package com.busuu.app.services.question.multiple_choice;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.questions.multiple_choice.MultipleChoiceOptionDTO;
import com.busuu.app.dtos.responses.question.multiple_choice.MultipleChoiceOptionResponse;
import com.busuu.app.entities.questions.multiple_choice.MultipleChoiceOption;
import com.busuu.app.entities.questions.multiple_choice.QuestionMultipleChoice;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.MultipleChoiceOptionRepository;
import com.busuu.app.repositories.QuestionMultipleChoiceRepository;
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
public class MultipleChoiceOptionService implements IMultipleChoiceOptionService {
    private final MultipleChoiceOptionRepository multipleChoiceOptionRepository;
    private final QuestionMultipleChoiceRepository questionMultipleChoiceRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public MultipleChoiceOptionResponse insertMultipleChoiceOption(String requestId, MultipleChoiceOptionDTO multipleChoiceOptionDTO) {
        try {
            QuestionMultipleChoice existingQuestion = questionMultipleChoiceRepository.findById(multipleChoiceOptionDTO.getQuestionMultipleChoiceId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot not find Question with ID = " + multipleChoiceOptionDTO.getQuestionMultipleChoiceId()));

            MultipleChoiceOption multipleChoiceOption = modelMapper.map(multipleChoiceOptionDTO, MultipleChoiceOption.class);
            multipleChoiceOption.setQuestionMultipleChoice(existingQuestion);

            multipleChoiceOption = multipleChoiceOptionRepository.save(multipleChoiceOption);

            MultipleChoiceOptionResponse response = modelMapper.map(multipleChoiceOption, MultipleChoiceOptionResponse.class);
            response.setQuestionId(multipleChoiceOption.getQuestionMultipleChoice().getId());

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to create multiple choice option, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_MULTIPLE_CHOICE, requestId);
        }
    }

    @Override
    public MultipleChoiceOptionResponse getMultipleChoiceOption(String requestId, String multipleChoiceId) {
        try {
            MultipleChoiceOption existingMultipleChoiceOption = multipleChoiceOptionRepository.findById(multipleChoiceId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Multiple Choice Option with ID = " + multipleChoiceId));

            MultipleChoiceOptionResponse response = modelMapper.map(existingMultipleChoiceOption, MultipleChoiceOptionResponse.class);
            response.setQuestionId(existingMultipleChoiceOption.getQuestionMultipleChoice().getId());

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to get multiple choice option, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_MULTIPLE_CHOICE, requestId);
        }
    }

    @Override
    public List<MultipleChoiceOptionResponse> getByQuestion(String requestId, String questionId) {
        try {
            QuestionMultipleChoice existingQuestionMultipleChoice = questionMultipleChoiceRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));
            List<MultipleChoiceOption> existingMultipleChoiceOptions = multipleChoiceOptionRepository.findByQuestionMultipleChoice(existingQuestionMultipleChoice);

            return existingMultipleChoiceOptions.stream().map(
                    multipleChoiceOption -> {
                        MultipleChoiceOptionResponse response = modelMapper.map(multipleChoiceOption, MultipleChoiceOptionResponse.class);
                        response.setQuestionId(multipleChoiceOption.getQuestionMultipleChoice().getId());
                        return response;
                    }
            ).toList();
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to get multiple choice option, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_MULTIPLE_CHOICE, requestId);
        }
    }

    @Override
    @Transactional
    public MultipleChoiceOptionResponse updateMultipleChoiceOption(String requestId, String multipleChoiceId, MultipleChoiceOptionDTO multipleChoiceOptionDTO) {
        try {
            MultipleChoiceOption existingMultipleChoiceOption = multipleChoiceOptionRepository.findById(multipleChoiceId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Matching Pair with ID = " + multipleChoiceId));

            if (!existingMultipleChoiceOption.getQuestionMultipleChoice().getId().equals(multipleChoiceOptionDTO.getQuestionMultipleChoiceId())) {
                QuestionMultipleChoice existingQuestion = questionMultipleChoiceRepository.findById(multipleChoiceOptionDTO.getQuestionMultipleChoiceId())
                        .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + multipleChoiceOptionDTO.getQuestionMultipleChoiceId()));
                existingMultipleChoiceOption.setQuestionMultipleChoice(existingQuestion);
            }
            modelMapper.map(multipleChoiceOptionDTO, existingMultipleChoiceOption);

            existingMultipleChoiceOption = multipleChoiceOptionRepository.save(existingMultipleChoiceOption);

            MultipleChoiceOptionResponse response = modelMapper.map(existingMultipleChoiceOption, MultipleChoiceOptionResponse.class);
            response.setQuestionId(existingMultipleChoiceOption.getQuestionMultipleChoice().getId());

            return response;
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to update multiple choice option, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_MULTIPLE_CHOICE, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteMultipleChoiceOption(String requestId, String multipleChoiceId) {
        try {
            multipleChoiceOptionRepository.deleteById(multipleChoiceId);
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to delete multiple choice option, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_MULTIPLE_CHOICE, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteByQuestionId(String requestId, String questionId) {
        try {
            QuestionMultipleChoice existingQuestionMultipleChoice = questionMultipleChoiceRepository.findById(questionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Question with ID = " + questionId));
            multipleChoiceOptionRepository.deleteByQuestionMultipleChoice(existingQuestionMultipleChoice);
        } catch (Exception e) {
            log.error("requestId="+requestId+", failed to delete multiple choice option, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_MULTIPLE_CHOICE, requestId);
        }
    }
}
