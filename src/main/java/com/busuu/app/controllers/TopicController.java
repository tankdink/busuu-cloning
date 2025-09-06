package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.level.LevelDTO;
import com.busuu.app.dtos.responses.LevelResponse;
import com.busuu.app.dtos.responses.PagingResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.dtos.responses.TopicResponse;
import com.busuu.app.entities.Level;
import com.busuu.app.services.level.ILevelService;
import com.busuu.app.services.topic.ITopicService;
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
@RequestMapping(Constants.TOPIC)
@RequiredArgsConstructor
@Slf4j
public class TopicController {

    private final ITopicService topicService;

    private final LocalizationUtils localizationUtils;


    @GetMapping()
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getTopics(@RequestParam(value = "req-id", required = false) String requestId,

                                              @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                              @RequestParam(value = "size", defaultValue = "10", required = false) int size,

                                              @RequestParam(value = "sort-by", required = false) List<String> sortBy,
                                              @RequestParam(value = "sort-direction", required = false) List<String> sortDirection,
                                              @RequestParam(value = "search-value", required = false) String searchValue,

                                              @RequestParam(value = "topic-type", required = false) String topicType,
                                              @RequestParam(value = "topic-category", required = false) String topicCategory) {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get all topics service
            List<TopicResponse> topicsList = topicService.getTopicsByType(requestId, topicType, page, size, sortBy, sortDirection, searchValue, topicCategory);

//            Object responseData = PagingResponse.<TopicResponse>builder()
//                    .totalPages(topicsList.getTotalPages())
//                    .objects(topicsList.getContent())
//                    .totalObjects(topicsList.getTotalElements())
//                    .build();

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .data(topicsList)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting topics list: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }
}