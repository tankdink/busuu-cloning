package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.level.LevelDTO;
import com.busuu.app.dtos.responses.LevelResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.entities.Level;
import com.busuu.app.services.level.ILevelService;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(Constants.LEVEL)
@RequiredArgsConstructor
@Slf4j
public class LevelController
{

    private final ILevelService levelService;

    private final LocalizationUtils localizationUtils;

    @PostMapping()
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertLevel(@RequestParam(value = "req-id", required = false) String requestId,
                                                @Valid @RequestBody LevelDTO newLevelDTO)
    {
        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call add level service
            LevelResponse addedLevel = levelService.insertLevel(requestId, newLevelDTO);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .status(HttpStatus.CREATED.value())
                            .data(addedLevel)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when adding new level: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping()
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getLevels(@RequestParam(value = "req-id", required = false) String requestId,
                                              @RequestParam(value = "code", required = false) String code,
                                              @RequestParam(value = "course_id", required = false) String courseId)
    {

        //This controller include 3 services: get level by code, get levels by courseId and get all levels

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            if ( (courseId != null && !courseId.isEmpty()) && (code != null && !code.isEmpty()) )
            {

                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": Invalid fetching condition: only courseId OR code can be pass in one time, or none of them is passed (fetch levels by courseId OR fetch level by code, fetch all levels)" )
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build()
                );

            }
            else if (code != null && !code.isEmpty())
            {
                //Call get level by code service
                LevelResponse level = levelService.getLevelByCode(requestId, code);

                //Return response
                return ResponseEntity.ok().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                                .status(HttpStatus.OK.value())
                                .data(level)
                                .build()
                );
            }
            else if (courseId != null && !courseId.isEmpty())
            {

                //Call get level by courseId
                List<LevelResponse> levelList = levelService.getLevelsByCourseId(requestId, courseId);

                //Return response
                return ResponseEntity.ok().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                                .status(HttpStatus.OK.value())
                                .data(levelList)
                                .build()
                );

            }
            else
            {

                //Call get all level service
                List<LevelResponse> levelList = levelService.getLevels(requestId);

                //Return response
                return ResponseEntity.ok().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                                .status(HttpStatus.OK.value())
                                .data(levelList)
                                .build()
                );

            }

        } catch (Exception e) {
            log.error("Error when getting level list: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getLevel(@RequestParam(value = "req-id", required = false) String requestId,
                                             @PathVariable("id") String levelId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get level by ID service
            LevelResponse gettedLevel = levelService.getLevel(requestId, levelId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .data(gettedLevel)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting level with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @PutMapping(Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateLevel(@RequestParam(value = "req-id", required = false) String requestId,
                                                @PathVariable("id") String levelId,
                                                @Valid @RequestBody LevelDTO infoUpdate)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call update level by ID service
            LevelResponse levelUpdated = levelService.updateLevel(requestId, levelId, infoUpdate);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .data(levelUpdated)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when updating level with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteLevel(@RequestParam(value = "req-id", required = false) String requestId,
                                                    @PathVariable("id") String levelId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call delete level by ID service
            levelService.deleteLevel(requestId, levelId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when deleting level: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }
}