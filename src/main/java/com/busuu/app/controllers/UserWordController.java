package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.word.ReviewResultRequest;
import com.busuu.app.dtos.responses.PagingResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.dtos.responses.UserWordResponse;
import com.busuu.app.dtos.responses.WordFilterResponse;
import com.busuu.app.dtos.responses.WordResponse;
import com.busuu.app.entities.StrengthLevel;
import com.busuu.app.entities.UserWord;
import com.busuu.app.services.userWord.UserWordService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.USER_WORD)
@RequiredArgsConstructor
@Slf4j
public class UserWordController {

    private final LocalizationUtils localizationUtils;

    private final UserWordService userWordService;

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> getWords (@RequestParam(value = "req-id", required = false) String requestId,
                                              @RequestParam(value = "strength-level", required = false, defaultValue = "") StrengthLevel strengthLevel,
                                              @RequestParam(value = "is-favorite", required = false) Boolean isFavorite,
                                              @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                              @RequestParam(value = "limit", defaultValue = "10", required = false) int limit) {

        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending()
        );

        Page<UserWordResponse> res = userWordService.getWordsByUser(requestId, pageRequest, strengthLevel, isFavorite);

        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                        .data(PagingResponse.<UserWordResponse>builder()
                                .totalPages(res.getTotalPages())
                                .objects(res.getContent())
                                .totalObjects(res.getTotalElements())
                                .build())
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @PutMapping(Constants.FAVORITE + Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> favoriteWord (@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String userWordId) {

        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        userWordService.favoriteWord(requestId, userWordId);

        return ResponseEntity.ok(
                Response.builder()
                        .data(null)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @DeleteMapping(Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> deleteWord (@RequestParam(value = "req-id", required = false) String requestId,
                                                @PathVariable("id") String wordId) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        userWordService.deleteWord(requestId, wordId);

        return ResponseEntity.ok(
                Response.builder()
                        .data(null)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }


    @GetMapping(Constants.FILTER)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> getListFilter (@RequestParam(value = "req-id", required = false) String requestId) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        List<WordFilterResponse> listFilter = userWordService.listFilter(requestId);

        return ResponseEntity.ok(
                Response.builder()
                        .data(listFilter)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping(Constants.REVIEW)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> getReviewWords (@RequestParam(value = "req-id", required = false) String requestId,
                                                    @RequestParam("type") String type,
                                                    @RequestParam(value = "strength-level", required = false) StrengthLevel strengthLevel) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        List<WordResponse> res = userWordService.getReviewWords(requestId, type, strengthLevel);

        return ResponseEntity.ok(
                Response.builder()
                        .data(res)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @PutMapping(Constants.REVIEW)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> processReviewResult(@RequestParam(value = "req-id", required = false) String requestId,
                                                         @Validated @RequestBody List<ReviewResultRequest> requests) {

        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        userWordService.processReviewResults(requests);

        return ResponseEntity.ok(
                Response.builder()
                        .data(null)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }
}
