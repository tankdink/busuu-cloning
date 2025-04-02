package com.busuu.app.controllers.question.ordering;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.questions.ordering.OrderingPartDTO;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.dtos.responses.question.ordering.OrderingPartResponse;
import com.busuu.app.services.question.ordering.IOrderingPartService;
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
@RequestMapping(Constants.ANSWER + Constants.ORDERING_PART)
@RequiredArgsConstructor
@Slf4j
public class OrderingPartController {
    private final IOrderingPartService orderingPartService;
    private final LocalizationUtils localizationUtils;

    @PostMapping()
    public ResponseEntity<Response> createOrderingPart (@RequestParam(value = "req-id", required = false) String requestId,
                                                        @Valid @RequestBody OrderingPartDTO orderingPartDTO,
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
            OrderingPartResponse res = orderingPartService.insertOrderingPart(requestId, orderingPartDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .data(res)
                            .status(HttpStatus.CREATED)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when create ordering part, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping(Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> getOrderingPart (@RequestParam(value = "req-id", required = false) String requestId,
                                                     @PathVariable("id") String orderingPartId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            OrderingPartResponse res = orderingPartService.getOrderingPart(requestId, orderingPartId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(res)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get ordering part, " + e.getMessage());
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

            List<OrderingPartResponse> res = orderingPartService.getByQuestion(requestId, questionId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(res)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get ordering part, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @PutMapping(Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> updateOrderingPart (@RequestParam(value = "req-id", required = false) String requestId,
                                                        @PathVariable("id") String orderingPartId,
                                                        @Valid @RequestBody OrderingPartDTO orderingPartDTO,
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

            OrderingPartResponse res = orderingPartService.updateOrderingPart(requestId, orderingPartId, orderingPartDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                            .data(res)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when update ordering part, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> deleteOrderingPart (@RequestParam(value = "req-id", required = false) String requestId,
                                                        @PathVariable("id") String orderingPartId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            orderingPartService.deleteOrderingPart(requestId, orderingPartId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when delete ordering part, " + e.getMessage());
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

            orderingPartService.deleteByQuestionId(requestId, questionId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when delete ordering part by question id, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

}
