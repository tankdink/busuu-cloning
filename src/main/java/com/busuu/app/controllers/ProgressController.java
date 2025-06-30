package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.progress.CourseProgressDTO;
import com.busuu.app.dtos.requests.progress.GrammarProgressDTO;
import com.busuu.app.dtos.requests.progress.ProgressDTO;
import com.busuu.app.dtos.responses.ProgressResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.services.progress.IProgressService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.PROGRESS)
@RequiredArgsConstructor
@Slf4j
public class ProgressController {

    private final IProgressService progressService;

    private final LocalizationUtils localizationUtils;

    @PostMapping(Constants.COURSE)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> upSertCourseProgress(@RequestParam(value = "req-id", required = false) String requestId,
                                                         @Valid @RequestBody CourseProgressDTO courseProgressDTO,
                                                         BindingResult result)
    {
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

            ProgressResponse progressResponse = progressService.upSertCourseProgress(
                    requestId, courseProgressDTO);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(progressResponse)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when upsert object progress: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @PostMapping(Constants.GRAMMAR)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> upSertGrammarProgress(@RequestParam(value = "req-id", required = false) String requestId,
                                                         @Valid @RequestBody GrammarProgressDTO grammarProgressDTO,
                                                         BindingResult result)
    {
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

            ProgressResponse progressResponse = progressService.upSertGrammarProgress(
                    requestId, grammarProgressDTO);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(progressResponse)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when upsert object progress: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

//    @PostMapping()
//    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
//    @PreAuthorize("hasRole('ROLE_USER')")
//    public ResponseEntity<Response> getObjectProgress(@RequestParam(value = "req-id", required = false) String requestId,
//                                                         @RequestParam("object_name") String objectName,
//                                                         @Valid @RequestBody ProgressDTO progressRequest,
//                                                         BindingResult result)
//    {
//        try {
//
//            if (requestId == null || requestId.isEmpty()) {
//                requestId = UUID.randomUUID().toString();
//            }
//
//            if (result.hasErrors()) {
//                List<String> errorMessages = result.getFieldErrors().stream()
//                        .map(FieldError::getDefaultMessage)
//                        .toList();
//
//                // Log error
//                log.error(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()));
//
//                return ResponseEntity.badRequest().body(
//                        Response.builder()
//                                .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
//                                .status(HttpStatus.BAD_REQUEST)
//                                .build()
//                );
//            }
//
//            ProgressResponse progressResponse = progressService.getObjectProgress(requestId, progressRequest.getObjectId(), progressRequest.getUserId(), objectName);
//
//            //Return response
//            return ResponseEntity.ok().body(
//                    Response.builder()
//                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
//                            .status(HttpStatus.OK)
//                            .data(progressResponse)
//                            .build()
//            );
//
//        } catch (Exception e) {
//            log.error("Error when get object progress: " + e.getMessage());
//            return ResponseEntity.badRequest().body(
//                    Response.builder()
//                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
//                            .status(HttpStatus.BAD_REQUEST)
//                            .build()
//            );
//        }
//    }


}
