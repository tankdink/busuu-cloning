package com.busuu.app.services.question;

import com.busuu.app.dtos.responses.question.QuestionResponse;
import com.busuu.app.repositories.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService implements IQuestionService {
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<QuestionResponse> getByLessonId(String requestId, String lessonId) {
        return null;
    }

    @Override
    public List<QuestionResponse> getByGrammarSectionId(String requestId, String grammarSectionId) {
        return null;
    }

    @Override
    @Transactional
    public void deleteByLessonId(String requestId, String lessonId) {

    }

    @Override
    @Transactional
    public void deleteByGrammarSectionId(String requestId, String grammarSectionId) {

    }
}
