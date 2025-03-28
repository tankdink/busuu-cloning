package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.section.GrammarSectionDTO;
import com.busuu.app.dtos.responses.GrammarSectionResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.services.grammarSection.IGrammarSectionService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(Constants.GRAMMAR_SECTION)
@RequiredArgsConstructor
@Slf4j
public class GrammarSectionController
{
    private final IGrammarSectionService grammarSectionService;

    private final LocalizationUtils localizationUtils;

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertGrammarSection(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @Valid @RequestBody GrammarSectionDTO newGrammarSectionDTO)
    {
        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call add grammarSection service
            GrammarSectionResponse addedGrammarSection = grammarSectionService.insertGrammarSection(requestId, newGrammarSectionDTO);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .status(HttpStatus.CREATED)
                            .data(addedGrammarSection)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when adding new grammar section: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED) +": "+ e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping()
    public ResponseEntity<Response> getGrammarSections(@RequestParam(value = "req-id", required = false) String requestId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get all grammarSection service
            List<GrammarSectionResponse> grammarSectionList = grammarSectionService.getGrammarSections(requestId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(grammarSectionList)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting grammar section list: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": "+ e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping(Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> getGrammarSection(@RequestParam(value = "req-id", required = false) String requestId,
                                               @PathVariable("id") String grammarSectionId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get grammarSection by ID service
            GrammarSectionResponse gettedGrammarSection = grammarSectionService.getGrammarSection(requestId, grammarSectionId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(gettedGrammarSection)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting grammar section with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping(Constants.GRAMMAR + Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> getByGrammarId(@RequestParam(value = "req-id", required = false) String requestId,
                                                            @PathVariable("id") String grammarId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get grammarSection by ID service
            List<GrammarSectionResponse> gettedGrammarSectionList = grammarSectionService.getByGrammarId(requestId, grammarId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(gettedGrammarSectionList)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting grammar section with grammar ID: " + e.getMessage());
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
    public ResponseEntity<Response> updateGrammarSection(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String grammarSectionId,
                                                  @Valid @RequestBody GrammarSectionDTO infoUpdate)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call update grammarSection by ID service
            GrammarSectionResponse grammarSectionUpdated = grammarSectionService.updateGrammarSection(requestId, grammarSectionId, infoUpdate);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .data(grammarSectionUpdated)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when updating grammar section with ID: " + e.getMessage());
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
    public ResponseEntity<Response> deleteGrammarSection(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String grammarSectionId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call delete grammar section by ID service
            grammarSectionService.deleteGrammarSection(requestId, grammarSectionId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when deleting grammar section: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED)+": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }
}
