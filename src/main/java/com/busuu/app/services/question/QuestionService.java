package com.busuu.app.services.question;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.question.QuestionFillBlankResponse;
import com.busuu.app.dtos.responses.question.QuestionResponse;
import com.busuu.app.dtos.responses.question.QuestionTrueFalseResponse;
import com.busuu.app.dtos.responses.question.matching.QuestionMatchingResponse;
import com.busuu.app.dtos.responses.question.multiple_choice.QuestionMultipleChoiceResponse;
import com.busuu.app.dtos.responses.question.ordering.QuestionOrderingResponse;
import com.busuu.app.entities.questions.Question;
import com.busuu.app.entities.questions.QuestionFillBlank;
import com.busuu.app.entities.questions.QuestionTrueFalse;
import com.busuu.app.entities.questions.QuestionType;
import com.busuu.app.entities.questions.matching.QuestionMatching;
import com.busuu.app.entities.questions.multiple_choice.QuestionMultipleChoice;
import com.busuu.app.entities.questions.ordering.QuestionOrdering;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.questions.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService implements IQuestionService {
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<QuestionResponse> getByLessonId(String requestId, String lessonId, int page, int size, String sortBy, String sortDirection) {
        try {

            //Pageable
            Sort sort;
            if (sortBy.equals("default"))
            {
                sort = Sort.by(
                        Sort.Order.by("grammarSectionId").with(Sort.Direction.fromString(sortDirection)),
                        Sort.Order.by("lessonId").with(Sort.Direction.fromString(sortDirection))
                );
            }
            else { sort = Sort.by(Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection))); }
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Question> questions = questionRepository.findByLessonId(lessonId, pageable);
            return convertQuestionResponse(questions);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get questions by lesson id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_QUESTION, requestId);
        }
    }

    @Override
    public Page<QuestionResponse> getByGrammarSectionId(String requestId, String grammarSectionId, int page, int size, String sortBy, String sortDirection) {
        try {

            //Pageable
            Sort sort;
            if (sortBy.equals("default"))
            {
                sort = Sort.by(
                        Sort.Order.by("grammarSectionId").with(Sort.Direction.fromString(sortDirection)),
                        Sort.Order.by("lessonId").with(Sort.Direction.fromString(sortDirection))
                );
            }
            else { sort = Sort.by(Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection))); }
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Question> questions = questionRepository.findByGrammarSectionId(grammarSectionId, pageable);
            return convertQuestionResponse(questions);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get questions by grammar section id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_QUESTION, requestId);
        }
    }

    @Override
    public QuestionResponse getById(String requestId, String questionId)
    {
        Question question = questionRepository.findById(questionId)
                .orElseThrow( ()-> new DataNotFoundException("Cannot find question with ID " + questionId) );

        QuestionResponse response = modelMapper.map(question, QuestionResponse.class);
        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());

        return response;
    }

    @Override
    public Page<QuestionResponse> getByQuestionType(String requestId, String questionType, int page, int size, String sortBy, String sortDirection)
    {
        try {

            //Convert String to enum before passing to repository
            QuestionType type = QuestionType.valueOf(questionType.toUpperCase());

            //Pageable
            Sort sort;
            if (sortBy.equals("default"))
            {
                sort = Sort.by(
                        Sort.Order.by("grammarSectionId").with(Sort.Direction.fromString(sortDirection)),
                        Sort.Order.by("lessonId").with(Sort.Direction.fromString(sortDirection))
                );
            }
            else { sort = Sort.by(Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection))); }
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Question> questions = questionRepository.findByQuestionType(type, pageable);
            return convertQuestionResponse(questions);

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get questions by grammar section id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteByLessonId(String requestId, String lessonId) {
        try {
            questionRepository.deleteByLessonId(lessonId);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete questions by lesson id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_QUESTION, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteByGrammarSectionId(String requestId, String grammarSectionId) {
        try {
            questionRepository.deleteByGrammarSectionId(grammarSectionId);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete questions by grammar section id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_QUESTION, requestId);
        }
    }

    private List<QuestionResponse> convertQuestionResponse(List<Question> questions) {
        return questions.stream().map(
                question -> {
                    if (question instanceof QuestionFillBlank) {
                        QuestionFillBlankResponse response = modelMapper.map(question, QuestionFillBlankResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionTrueFalse) {
                        QuestionTrueFalseResponse response = modelMapper.map(question, QuestionTrueFalseResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionOrdering) {
                        QuestionOrderingResponse response = modelMapper.map(question, QuestionOrderingResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionMultipleChoice) {
                        QuestionMultipleChoiceResponse response = modelMapper.map(question, QuestionMultipleChoiceResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionMatching) {
                        QuestionMatchingResponse response = modelMapper.map(question, QuestionMatchingResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else
                    {
                        QuestionResponse response = modelMapper.map(question, QuestionResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }

                }
        ).toList();
    }

    private Page<QuestionResponse> convertQuestionResponse(Page<Question> questions) {
        return questions.map(
                question -> {
                    if (question instanceof QuestionFillBlank) {
                        QuestionFillBlankResponse response = modelMapper.map(question, QuestionFillBlankResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionTrueFalse) {
                        QuestionTrueFalseResponse response = modelMapper.map(question, QuestionTrueFalseResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionOrdering) {
                        QuestionOrderingResponse response = modelMapper.map(question, QuestionOrderingResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionMultipleChoice) {
                        QuestionMultipleChoiceResponse response = modelMapper.map(question, QuestionMultipleChoiceResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionMatching) {
                        QuestionMatchingResponse response = modelMapper.map(question, QuestionMatchingResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else
                    {
                        QuestionResponse response = modelMapper.map(question, QuestionResponse.class);
                        if (question.getLesson() != null ) response.setLessonId(question.getLesson().getId());
                        if (question.getGrammarSection() != null ) response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }

                    //return null;
                }
        );
    }
}
