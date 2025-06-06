package com.busuu.app.controllers.question;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.PagingResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.dtos.responses.question.QuestionResponse;
import com.busuu.app.services.question.IQuestionService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
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

    @GetMapping(Constants.QUESTION_TYPE)
    public ResponseEntity<Response> getQuestionsByQuestionType(@RequestParam(value = "req-id", required = false) String requestId,
                                                               @PathVariable("type") String questionType,

                                                               @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                               @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                               @RequestParam(value = "sort_by", defaultValue = "default", required = false) String sortBy,
                                                               @RequestParam(value = "sort_direction", defaultValue = "ASC", required = false) String sortDirection)
    {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            Page<QuestionResponse> questionResponses = questionService.getByQuestiontype(requestId, questionType, page, size, sortBy, sortDirection);

            Object responseData = PagingResponse.<QuestionResponse>builder()
                    .totalPages(questionResponses.getTotalPages())
                    .objects(questionResponses.getContent())
                    .totalObjects(questionResponses.getTotalElements())
                    .build();

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(responseData)
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
