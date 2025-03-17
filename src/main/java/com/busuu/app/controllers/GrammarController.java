package com.busuu.app.controllers;


import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.grammar.GrammarDTO;
import com.busuu.app.dtos.responses.GrammarResponse;
import com.busuu.app.dtos.responses.GrammarResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.services.grammar.IGrammarService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertGrammar(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @Valid @RequestBody GrammarDTO newGrammarDTO)
    {
        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call add grammar service
            GrammarResponse addedGrammar = grammarService.insertGrammar(requestId, newGrammarDTO);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .status(HttpStatus.CREATED)
                            .data(addedGrammar)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when adding new grammar: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED) +": "+ e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping()
    public ResponseEntity<Response> getGrammars(@RequestParam(value = "req-id", required = false) String requestId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get all grammar service
            List<GrammarResponse> grammarList = grammarService.getGrammars(requestId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(grammarList)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting grammar list: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": "+ e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping(Constants.PATH_PARAM_ID)
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
                            .status(HttpStatus.OK)
                            .data(gettedGrammar)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting grammar with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping(Constants.LANGUAGE + Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> getByLanguageId(@RequestParam(value = "req-id", required = false) String requestId,
                                                    @PathVariable("id") String languageId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get chapter by ID service
            List<GrammarResponse> gettedGrammarList = grammarService.getByLanguageId(requestId, languageId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(gettedGrammarList)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting chapter with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }


    @PutMapping(Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateGrammar(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String grammarId,
                                                  @Valid @RequestBody GrammarDTO infoUpdate)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call update grammar by ID service
            GrammarResponse grammarUpdated = grammarService.updateGrammar(requestId, grammarId, infoUpdate);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .data(grammarUpdated)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when updating grammar with ID: " + e.getMessage());
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
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when deleting grammar: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED)+": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }
}
