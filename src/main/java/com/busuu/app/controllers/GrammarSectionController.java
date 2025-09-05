package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.section.GrammarSectionDTO;
import com.busuu.app.dtos.responses.GrammarResponse;
import com.busuu.app.dtos.responses.GrammarSectionResponse;
import com.busuu.app.dtos.responses.PagingResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.services.grammarSection.IGrammarSectionService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
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
                            .status(HttpStatus.CREATED.value())
                            .data(addedGrammarSection)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when adding new grammar section: " + e.getMessage());
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
    public ResponseEntity<Response> getGrammarSections(@RequestParam(value = "req-id", required = false) String requestId,

                                                       @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                       @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                       @RequestParam(value = "sort_by", defaultValue = "grammar_section_id", required = false) String sortBy,
                                                       @RequestParam(value = "sort_direction", defaultValue = "ASC", required = false) String sortDirection)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get all grammarSection service
            Page<GrammarSectionResponse> grammarSectionList = grammarSectionService.getGrammarSections(requestId, page, size, sortBy, sortDirection);
            Object responseData = PagingResponse.<GrammarSectionResponse>builder()
                    .totalPages(grammarSectionList.getTotalPages())
                    .objects(grammarSectionList.getContent())
                    .totalObjects(grammarSectionList.getTotalElements())
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
            log.error("Error when getting grammar section list: " + e.getMessage());
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
                            .status(HttpStatus.OK.value())
                            .data(gettedGrammarSection)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting grammar section with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.GRAMMAR + Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
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
                            .status(HttpStatus.OK.value())
                            .data(gettedGrammarSectionList)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting grammar section with grammar ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @PutMapping(Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
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
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when updating grammar section with ID: " + e.getMessage());
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
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when deleting grammar section: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED)+": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }
}
