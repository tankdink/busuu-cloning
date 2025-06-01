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
import com.busuu.app.entities.questions.matching.QuestionMatching;
import com.busuu.app.entities.questions.multiple_choice.QuestionMultipleChoice;
import com.busuu.app.entities.questions.ordering.QuestionOrdering;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.questions.QuestionRepository;
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
public class QuestionService implements IQuestionService {
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<QuestionResponse> getByLessonId(String requestId, String lessonId) {
        try {
            List<Question> questions = questionRepository.findByLessonId(lessonId);
            return convertQuestionResponse(questions);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get questions by lesson id, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_QUESTION, requestId);
        }
    }

    @Override
    public List<QuestionResponse> getByGrammarSectionId(String requestId, String grammarSectionId) {
        try {
            List<Question> questions = questionRepository.findByGrammarSectionId(grammarSectionId);
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
                        response.setLessonId(question.getLesson().getId());
                        response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionTrueFalse) {
                        QuestionTrueFalseResponse response = modelMapper.map(question, QuestionTrueFalseResponse.class);
                        response.setLessonId(question.getLesson().getId());
                        response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionOrdering) {
                        QuestionOrderingResponse response = modelMapper.map(question, QuestionOrderingResponse.class);
                        response.setLessonId(question.getLesson().getId());
                        response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionMultipleChoice) {
                        QuestionMultipleChoiceResponse response = modelMapper.map(question, QuestionMultipleChoiceResponse.class);
                        response.setLessonId(question.getLesson().getId());
                        response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    else if (question instanceof QuestionMatching) {
                        QuestionMatchingResponse response = modelMapper.map(question, QuestionMatchingResponse.class);
                        response.setLessonId(question.getLesson().getId());
                        response.setGrammarSectionId(question.getGrammarSection().getId());
                        return response;
                    }
                    return null;
                }
        ).toList();
    }
}
