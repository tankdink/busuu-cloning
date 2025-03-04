package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.LevelDTO;
import com.busuu.app.dtos.requests.user.UserDTO;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.entities.Level;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.services.level.ILevelService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Controller
@RestController
@RequestMapping(Constants.LEVEL)
@RequiredArgsConstructor
@Slf4j
public class LevelController
{

    private final ILevelService levelService;

    private final LocalizationUtils localizationUtils;

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertLevel(@RequestParam(value = "req-id", required = false) String requestId,
                                                @Valid @RequestBody LevelDTO newLevelDTO)
    {
        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call add level service
            Level addedLevel = levelService.insertLevel(requestId, newLevelDTO);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.CREATE_NEW_LEVEL_SUCCESSFULLY))
                            .status(HttpStatus.CREATED)
                            .data(addedLevel)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when adding new level: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.ERROR_CREATE_NEW_LEVEL))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping()
    public ResponseEntity<Response> getLevels(@RequestParam(value = "req-id", required = false) String requestId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get all level service
            List<Level> levelList = levelService.getLevels(requestId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_ALL_LEVEL_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .data(levelList)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting level list: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.ERROR_CREATE_NEW_LEVEL))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping(Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> getLevel(@RequestParam(value = "req-id", required = false) String requestId,
                                             @PathVariable("id") String levelId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get level by ID service
            Level gettedLevel = levelService.getLevel(requestId, levelId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_LEVEL_BY_ID_SUCCESSFULLY) + ":" + levelId)
                            .status(HttpStatus.OK)
                            .data(gettedLevel)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting level with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.ERROR_GET_LEVEL_BY_ID))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @PutMapping(Constants.PATH_PARAM_ID)
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
            levelService.updateLevel(requestId, levelId, infoUpdate);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_LEVEL_BY_ID_SUCCESSFULLY) + ":" + levelId)
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when updating level with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.ERROR_UPDATE_LEVEL_BY_ID))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteLevelById(@RequestParam(value = "req-id", required = false) String requestId,
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
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_LEVEL_BY_ID_SUCCESSFULLY) + ":" + levelId)
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting level list: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.ERROR_DELETE_LEVEL_BY_ID))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @DeleteMapping()
    @PreAuthorize("hasRole='ROLE_ADMIN")
    public ResponseEntity<Response> deleteLevels(@RequestParam(value = "req-id", required = false) String requestId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call delete all level service
            levelService.deleteLevels(requestId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_ALL_LEVEL_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting level list: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.ERROR_DELETE_ALL_LEVEL))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }
}