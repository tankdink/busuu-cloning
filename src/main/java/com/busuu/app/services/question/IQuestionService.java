package com.busuu.app.services.question;

import com.busuu.app.dtos.requests.questions.QuestionDTO;
import com.busuu.app.dtos.responses.question.QuestionResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IQuestionService {

    Page<QuestionResponse> getByLessonId (String requestId, String lessonId, int page, int size, String sortBy, String sortDirection);

    Page<QuestionResponse> getByGrammarSectionId (String requestId, String grammarSectionId, int page, int size, String sortBy, String sortDirection);

    QuestionResponse getById(String requestId, String questionId);

    Page<QuestionResponse> getByQuestionType (String requestId, String questionType, int page, int size, String sortBy, String sortDirection);

    void deleteByLessonId (String requestId, String lessonId);

    void deleteByGrammarSectionId (String requestId, String grammarSectionId);

    void deleteByQuestionId (String requestId, String questionId);

    Page<QuestionResponse> extractQuestionFileWord(MultipartFile file, int page, int size) throws IOException;

    Page<QuestionResponse> extractQuestionFilePDF(MultipartFile file, int page, int size) throws IOException;

    List<Map<String, Object>> extractQuestionFileExcel(MultipartFile file, int page, int size) throws IOException;
}
