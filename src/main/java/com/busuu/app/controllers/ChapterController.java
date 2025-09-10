package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.chapter.ChapterDTO;
import com.busuu.app.dtos.responses.ChapterResponse;
import com.busuu.app.dtos.responses.PagingResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.services.chapter.IChapterService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping(Constants.CHAPTER)
@RequiredArgsConstructor
@Slf4j
public class ChapterController {
    private final IChapterService chapterService;

    private final LocalizationUtils localizationUtils;

    @PostMapping()
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertChapter(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @Valid @RequestBody ChapterDTO newChapterDTO) {
        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call add chapter service
            ChapterResponse addedChapter = chapterService.insertChapter(requestId, newChapterDTO);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .status(HttpStatus.CREATED.value())
                            .data(addedChapter)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when adding new chapter: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }


    @GetMapping(Constants.PATH_PARAM_ID)
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getChapter(@RequestParam(value = "req-id", required = false) String requestId,
                                               @PathVariable("id") String chapterId) {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get chapter by ID service
            ChapterResponse gettedChapter = chapterService.getChapter(requestId, chapterId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .data(gettedChapter)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting chapter with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping()
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> getListChapter(@RequestParam(value = "req-id", required = false) String requestId,

                                                   @RequestParam(value = "course_id", required = false) String courseId,
                                                   @RequestParam(value = "level_id", required = false) String levelId,

                                                   @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                   @RequestParam(value = "size", defaultValue = "10", required = false) int size,

                                                   @RequestParam(value = "sort_by", required = false) List<String> sortBy,
                                                   @RequestParam(value = "sort_direction", required = false) List<String> sortDirection,
                                                   @RequestParam(value = "search_value", required = false) String searchValue) {

        //Including get chapters by courseId and levelId; get all chapters
        try {

            List<ChapterResponse> gettedChapterList = null;
            Object responseData;

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            if ((courseId != null && !courseId.isEmpty()) && (levelId != null && !levelId.isEmpty())) {

                //Call get chapter by courseID and levelID service
                gettedChapterList = chapterService.getByCourseIdAndLevelId(requestId, courseId, levelId);
                responseData = gettedChapterList;

            } else if ((courseId != null && !courseId.isEmpty()) || (levelId != null && !levelId.isEmpty())) {

                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": Invalid fetching condition: courseId and levelId is BOTH required (fetch chapters by courseId and levelId), or BOTH null (fetch all chapters)!")
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build()
                );

            } else {
                //Call get all chapters service
                Page<ChapterResponse> gettedChapterPage = chapterService.getChapters(requestId, page, size, sortBy, sortDirection, searchValue, courseId, levelId);
                responseData = PagingResponse.<ChapterResponse>builder()
                        .totalPages(gettedChapterPage.getTotalPages())
                        .objects(gettedChapterPage.getContent())
                        .totalObjects(gettedChapterPage.getTotalElements())
                        .build();
            }

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
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.LANGUAGE)
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> getChapters(@RequestParam(value = "req-id", required = false) String requestId,

                                                @RequestParam(value = "course_id") String courseId,
                                                @RequestParam(value = "level_id") String levelId) {

        //Including get chapters by courseId and levelId; get all chapters
        try {

            List<ChapterResponse> gettedChapterList = null;
            Object responseData;

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get chapter by courseID and levelID service
            gettedChapterList = chapterService.getByCourseIdAndLevelId(requestId, courseId, levelId);
            responseData = gettedChapterList;


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
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @PutMapping(Constants.PATH_PARAM_ID)
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateChapter(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String chapterId,
                                                  @Valid @RequestBody ChapterDTO infoUpdate) {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call update chapter by ID service
            ChapterResponse chapterUpdated = chapterService.updateChapter(requestId, chapterId, infoUpdate);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .data(chapterUpdated)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when updating chapter with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.PATH_PARAM_ID)
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteChapter(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String chapterId) {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call delete chapter by ID service
            chapterService.deleteChapter(requestId, chapterId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when deleting chapter: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

}
