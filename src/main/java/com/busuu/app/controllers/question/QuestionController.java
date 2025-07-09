package com.busuu.app.controllers.question;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.PagingResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.dtos.responses.question.QuestionResponse;
import com.busuu.app.services.question.IQuestionService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping(Constants.QUESTION)
@RequiredArgsConstructor
@Slf4j
public class QuestionController {
    private final IQuestionService questionService;
    private final LocalizationUtils localizationUtils;

    @GetMapping(Constants.LESSON + Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getQuestionsByLesson (@RequestParam(value = "req-id", required = false) String requestId,
                                                          @PathVariable("id") String lessonId,
                                                          @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                          @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                          @RequestParam(value = "sort_by", defaultValue = "default", required = false) String sortBy,
                                                          @RequestParam(value = "sort_direction", defaultValue = "ASC", required = false) String sortDirection)
    {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

           Page<QuestionResponse> questionResponses = questionService.getByLessonId(requestId, lessonId, page, size, sortBy, sortDirection);

            Object responseData = PagingResponse.<QuestionResponse>builder()
                    .totalPages(questionResponses.getTotalPages())
                    .objects(questionResponses.getContent())
                    .totalObjects(questionResponses.getTotalElements())
                    .build();

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .data(responseData)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting questions with lesson ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.GRAMMAR_SECTION + Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getQuestionsByGrammarSection (@RequestParam(value = "req-id", required = false) String requestId,
                                                                  @PathVariable("id") String grammarSectionId,

                                                                  @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                                  @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                  @RequestParam(value = "sort_by", defaultValue = "default", required = false) String sortBy,
                                                                  @RequestParam(value = "sort_direction", defaultValue = "ASC", required = false) String sortDirection)
    {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            Page<QuestionResponse> questionResponses = questionService.getByGrammarSectionId(requestId, grammarSectionId, page, size, sortBy, sortDirection);
            Object responseData = PagingResponse.<QuestionResponse>builder()
                    .totalPages(questionResponses.getTotalPages())
                    .objects(questionResponses.getContent())
                    .totalObjects(questionResponses.getTotalElements())
                    .build();


            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .data(responseData)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting questions with grammar section ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.QUESTION_TYPE)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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

            Page<QuestionResponse> questionResponses = questionService.getByQuestionType(requestId, questionType, page, size, sortBy, sortDirection);

            Object responseData = PagingResponse.<QuestionResponse>builder()
                    .totalPages(questionResponses.getTotalPages())
                    .objects(questionResponses.getContent())
                    .totalObjects(questionResponses.getTotalElements())
                    .build();

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .data(responseData)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting questions with question type: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    //This controller should be used for question with type KNOWLEDGE only, if use with other question type, it won't return the question's result
    @GetMapping(Constants.KNOWLEDGE + Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getQuestionKnowledgeById(@RequestParam(value = "req-id", required = false) String requestId,
                                                          @PathVariable("id") String questionId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            QuestionResponse questionResponses = questionService.getById(requestId, questionId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .data(questionResponses)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting questions with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.LESSON + Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
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
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when deleting questions with lesson ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.GRAMMAR_SECTION + Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
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
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when deleting questions with grammar section ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteQuestionById (@RequestParam(value = "req-id", required = false) String requestId,
                                                        @PathVariable("id") String questionId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            questionService.deleteByQuestionId(requestId, questionId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when deleting question with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @PostMapping(value = Constants.FILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> extractQuestionByFile(@RequestParam(value = "req-id", required = false) String requestId,
                                                          @RequestParam("file") MultipartFile file,
                                                          @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                          @RequestParam(value = "size", defaultValue = "10", required = false) int size)
    {
        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            String fileType = detectFileType(file);
            switch(fileType) {
                case "EXCEL":
                {
                    Page<QuestionResponse> extractedDataList = questionService.extractQuestionFileExcel(file, page, size);

                    Object responseData = PagingResponse.<QuestionResponse>builder()
                            .totalPages(extractedDataList.getTotalPages())
                            .objects(extractedDataList.getContent())
                            .totalObjects(extractedDataList.getTotalElements())
                            .build();

                    return ResponseEntity.ok(
                            Response.builder()
                                    .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                                    .data(responseData)
                                    .status(HttpStatus.OK.value())
                                    .build()
                    );
                }
                case "WORD":
                {
                    Page<QuestionResponse> extractedDataList = questionService.extractQuestionFileWord(file, page, size);

                    Object responseData = PagingResponse.<QuestionResponse>builder()
                            .totalPages(extractedDataList.getTotalPages())
                            .objects(extractedDataList.getContent())
                            .totalObjects(extractedDataList.getTotalElements())
                            .build();

                    return ResponseEntity.ok(
                            Response.builder()
                                    .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                                    .data(responseData)
                                    .status(HttpStatus.OK.value())
                                    .build()
                    );
                }
                case "PDF":
                {
                    Page<QuestionResponse> extractedDataList = questionService.extractQuestionFilePDF(file, page, size);

                    Object responseData = PagingResponse.<QuestionResponse>builder()
                            .totalPages(extractedDataList.getTotalPages())
                            .objects(extractedDataList.getContent())
                            .totalObjects(extractedDataList.getTotalElements())
                            .build();

                    return ResponseEntity.ok(
                            Response.builder()
                                    .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                                    .data(responseData)
                                    .status(HttpStatus.OK.value())
                                    .build()
                    );
                }
                default:
                {
                    return ResponseEntity.ok(
                            Response.builder()
                                    .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": File type is not supported! (Supported file types: .xlsx, .xls, .docx, .pdf (text-based pdf only)")
                                    .data(null)
                                    .status(HttpStatus.OK.value())
                                    .build()
                    );
                }
            }


        } catch (Exception e) {
            log.error("Error when extract question with file, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    public String detectFileType(MultipartFile file)
    {
        // Check content-type header first to detect file type
        String ct = file.getContentType();
        if (ct != null) {
            if (ct.equals("application/pdf"))
                return "PDF";
            if (ct.equals("application/vnd.ms-excel") || ct.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                return "EXCEL";
            if (ct.equals("application/msword") || ct.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                return "WORD";
        }


        // If there is no content-type header, check the file name extension instead
        String name = file.getOriginalFilename().toLowerCase();
        if (name.endsWith(".pdf")) return "PDF";
        if (name.endsWith(".xls") || name.endsWith(".xlsx")) return "EXCEL";
        if (name.endsWith(".doc") || name.endsWith(".docx")) return "WORD";
        return "UNSUPPORTED";
    }
}
