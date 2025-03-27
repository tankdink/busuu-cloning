package com.busuu.app.controllers.question.multiple_choice;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.questions.multiple_choice.MultipleChoiceOptionDTO;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.dtos.responses.question.multiple_choice.MultipleChoiceOptionResponse;
import com.busuu.app.services.question.multiple_choice.IMultipleChoiceOptionService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.ANSWER + Constants.MULTIPLE_CHOICE)
@RequiredArgsConstructor
@Slf4j
public class MultipleChoiceOptionController {
    private final IMultipleChoiceOptionService multipleChoiceOptionService;
    private final LocalizationUtils localizationUtils;

    @PostMapping()
    public ResponseEntity<Response> createMultipleChoiceOption (@RequestParam(value = "req-id", required = false) String requestId,
                                                                @Valid @RequestBody MultipleChoiceOptionDTO multipleChoiceOptionDTO,
                                                                BindingResult result) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                // Log error
                log.error(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()));

                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
            }
            MultipleChoiceOptionResponse res = multipleChoiceOptionService.insertMultipleChoiceOption(requestId, multipleChoiceOptionDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .data(res)
                            .status(HttpStatus.CREATED)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when create multiple choice option, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping(Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> getMultipleChoiceOption (@RequestParam(value = "req-id", required = false) String requestId,
                                                     @PathVariable("id") String multipleChoiceOptionId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            MultipleChoiceOptionResponse res = multipleChoiceOptionService.getMultipleChoiceOption(requestId, multipleChoiceOptionId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(res)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get multiple choice option, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping(Constants.QUESTION + Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> getByQuestionId (@RequestParam(value = "req-id", required = false) String requestId,
                                                     @PathVariable("id") String questionId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            List<MultipleChoiceOptionResponse> res = multipleChoiceOptionService.getByQuestion(requestId, questionId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(res)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get multiple choice option, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @PutMapping(Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> updateMultipleChoiceOption (@RequestParam(value = "req-id", required = false) String requestId,
                                                        @PathVariable("id") String multipleChoiceOptionId,
                                                        @Valid @RequestBody MultipleChoiceOptionDTO multipleChoiceOptionDTO,
                                                        BindingResult result) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                // Log error
                log.error(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()));

                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
            }

            MultipleChoiceOptionResponse res = multipleChoiceOptionService.updateMultipleChoiceOption(requestId, multipleChoiceOptionId, multipleChoiceOptionDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                            .data(res)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when update multiple choice option, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> deleteMultipleChoiceOption (@RequestParam(value = "req-id", required = false) String requestId,
                                                        @PathVariable("id") String multipleChoiceOptionId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            multipleChoiceOptionService.deleteMultipleChoiceOption(requestId, multipleChoiceOptionId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when delete multiple choice option, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.QUESTION + Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> deleteByQuestion (@RequestParam(value = "req-id", required = false) String requestId,
                                                      @PathVariable("id") String questionId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            multipleChoiceOptionService.deleteByQuestionId(requestId, questionId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when delete multiple choice option by question id, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }
}
