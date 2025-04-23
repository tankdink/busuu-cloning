package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.language.LanguageDTO;
import com.busuu.app.dtos.responses.LanguageResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.entities.Language;
import com.busuu.app.services.language.ILanguageService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.LANGUAGE)
@RequiredArgsConstructor
@Slf4j
public class LanguageController 
{
    private final ILanguageService languageService;

    private final LocalizationUtils localizationUtils;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertLanguage(@RequestParam(value = "req-id", required = false) String requestId,
                                                   @Valid @ModelAttribute LanguageDTO newLanguageDTO,
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

            //Call add language service
            LanguageResponse addedLanguage = languageService.insertLanguage(requestId, newLanguageDTO);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .status(HttpStatus.CREATED)
                            .data(addedLanguage)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when adding new language: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED) +": "+ e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping()
    public ResponseEntity<Response> getLanguages(@RequestParam(value = "req-id", required = false) String requestId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get all language service
            List<LanguageResponse> languageList = languageService.getLanguages(requestId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(languageList)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting language list: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": "+ e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping(Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> getLanguage(@RequestParam(value = "req-id", required = false) String requestId,
                                               @PathVariable("id") String languageId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get language by ID service
            LanguageResponse gettedLanguage = languageService.getLanguage(requestId, languageId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(gettedLanguage)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting language with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @PutMapping(value = Constants.PATH_PARAM_ID, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateLanguage(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String languageId,
                                                  @Valid @ModelAttribute LanguageDTO infoUpdate,
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

            //Call update language by ID service
            LanguageResponse languageUpdated = languageService.updateLanguage(requestId, languageId, infoUpdate);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .data(languageUpdated)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when updating language with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_FAILED) +": "+ e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteLanguage(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String languageId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call delete language by ID service
            languageService.deleteLanguage(requestId, languageId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when deleting language: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED)+": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }
}
