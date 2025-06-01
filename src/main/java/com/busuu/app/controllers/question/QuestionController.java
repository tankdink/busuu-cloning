package com.busuu.app.controllers.question;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.dtos.responses.question.QuestionResponse;
import com.busuu.app.services.question.IQuestionService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(Constants.QUESTION)
@RequiredArgsConstructor
@Slf4j
public class QuestionController {
    private final IQuestionService questionService;
    private final LocalizationUtils localizationUtils;

    @GetMapping(Constants.LESSON + Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> getQuestionsByLesson (@RequestParam(value = "req-id", required = false) String requestId,
                                                          @PathVariable("id") String lessonId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

           List<QuestionResponse> questionResponses = questionService.getByLessonId(requestId, lessonId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(questionResponses)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting questions with lesson ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping(Constants.GRAMMAR_SECTION + Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> getQuestionsByGrammarSection (@RequestParam(value = "req-id", required = false) String requestId,
                                                                  @PathVariable("id") String grammarSectionId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            List<QuestionResponse> questionResponses = questionService.getByGrammarSectionId(requestId, grammarSectionId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(questionResponses)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting questions with grammar section ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.LESSON + Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteQuestionsByLesson (@RequestParam(value = "req-id", required = false) String requestId,
                                                             @PathVariable("id") String lessonId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            questionService.deleteByLessonId(requestId, lessonId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when deleting questions with lesson ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.GRAMMAR_SECTION + Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteQuestionsByGrammarSection (@RequestParam(value = "req-id", required = false) String requestId,
                                                                     @PathVariable("id") String grammarSectionId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            questionService.deleteByGrammarSectionId(requestId, grammarSectionId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when deleting questions with grammar section ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }
}
