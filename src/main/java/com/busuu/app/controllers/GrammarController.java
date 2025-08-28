package com.busuu.app.controllers;


import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.grammar.GrammarDTO;
import com.busuu.app.dtos.responses.GrammarResponse;
import com.busuu.app.dtos.responses.GrammarResponse;
import com.busuu.app.dtos.responses.PagingResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.services.grammar.IGrammarService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.GRAMMAR)
@RequiredArgsConstructor
@Slf4j
public class GrammarController
{
    private final IGrammarService grammarService;

    private final LocalizationUtils localizationUtils;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertGrammar(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @Valid @ModelAttribute GrammarDTO newGrammarDTO,
                                                  BindingResult result)
    {
        try {

            if (requestId == null || requestId.isEmpty())
                requestId = UUID.randomUUID().toString();

            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                // Log error
                log.error(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()));

                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build()
                );
            }


            //Call add grammar service
            GrammarResponse addedGrammar = grammarService.insertGrammar(requestId, newGrammarDTO);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .status(HttpStatus.CREATED.value())
                            .data(addedGrammar)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when adding new grammar: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED) +": "+ e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping()
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getGrammars(@RequestParam(value = "req-id", required = false) String requestId,

                                                @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                @RequestParam(value = "size", defaultValue = "10", required = false) int size,

                                                @RequestParam(value = "sort_by", required = false) List<String> sortBy,
                                                @RequestParam(value = "sort_direction", required = false) List<String> sortDirection,
                                                @RequestParam(value = "search_value",required = false) String searchValue,

                                                @RequestParam(value = "filter_by",required = false) String language)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get all grammar service
            Page<GrammarResponse> grammarList = grammarService.getGrammars(requestId, page, size, sortBy, sortDirection, searchValue, language);
            Object responseData = PagingResponse.<GrammarResponse>builder()
                    .totalPages(grammarList.getTotalPages())
                    .objects(grammarList.getContent())
                    .totalObjects(grammarList.getTotalElements())
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
            log.error("Error when getting grammar list: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": "+ e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getGrammar(@RequestParam(value = "req-id", required = false) String requestId,
                                             @PathVariable("id") String grammarId)
    {

        try {


            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get grammar by ID service
            GrammarResponse gettedGrammar = grammarService.getGrammar(requestId, grammarId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .data(gettedGrammar)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting grammar with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.LANGUAGE + Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getByLanguageId(@RequestParam(value = "req-id", required = false) String requestId,
                                                    @PathVariable("id") String languageId,

                                                    @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                    @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                    @RequestParam(value = "sort_by", defaultValue = "grammarOrder", required = false) String sortBy,
                                                    @RequestParam(value = "sort_direction", defaultValue = "ASC", required = false) String sortDirection)

    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get chapter by ID service
            Page<GrammarResponse> gettedGrammarList = grammarService.getByLanguageId(requestId, languageId, page, size, sortBy, sortDirection);

            Object responseData = PagingResponse.<GrammarResponse>builder()
                    .totalPages(gettedGrammarList.getTotalPages())
                    .objects(gettedGrammarList.getContent())
                    .totalObjects(gettedGrammarList.getTotalElements())
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
            log.error("Error when getting chapter with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }


    @PutMapping(value = Constants.PATH_PARAM_ID, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateGrammar(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String grammarId,
                                                  @Valid @ModelAttribute GrammarDTO infoUpdate,
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
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build()
                );
            }

            //Call update grammar by ID service
            GrammarResponse grammarUpdated = grammarService.updateGrammar(requestId, grammarId, infoUpdate);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .data(grammarUpdated)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when updating grammar with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_FAILED) +": "+ e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteGrammar(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String grammarId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call delete grammar by ID service
            grammarService.deleteGrammar(requestId, grammarId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when deleting grammar: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED)+": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }
}
