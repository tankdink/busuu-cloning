package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.word.WordDTO;
import com.busuu.app.dtos.responses.PagingResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.dtos.responses.WordResponse;
import com.busuu.app.entities.User;
import com.busuu.app.services.word.IWordService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.WORD)
@RequiredArgsConstructor
@Slf4j
public class WordController {

    private final IWordService wordService;
    private final LocalizationUtils localizationUtils;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertWord (@RequestParam(value = "req-id", required = false) String requestId,
                                                @RequestBody @ModelAttribute WordDTO wordDTO) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        WordResponse word = wordService.createWord(requestId, wordDTO);

        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                        .data(word)
                        .status(HttpStatus.CREATED.value())
                        .build()
        );
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> getWords (@RequestParam(value = "req-id", required = false) String requestId,
                                              @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                              @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                              @RequestParam(value = "lesson-id", defaultValue = "null", required = false) String lessonId,
                                              @RequestParam(value = "keyword", defaultValue = "null", required = false) String keyword,
                                              @RequestParam(value = "sort-by", defaultValue = "id", required = false) String sortBy,
                                              @RequestParam(value = "sort-dir", defaultValue = "asc", required = false) String sortDir) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        List<String> allowedSortFields = List.of("id", "text", "created-at");
        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "id"; // Default
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<WordResponse> res = wordService.getWords(requestId, pageRequest, lessonId, keyword);

        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                        .data(PagingResponse.<WordResponse>builder()
                                .totalPages(res.getTotalPages())
                                .objects(res.getContent())
                                .totalObjects(res.getTotalElements())
                                .build())
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @PutMapping(path = Constants.PATH_PARAM_ID, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateWord (@RequestParam(value = "req-id", required = false) String requestId,
                                                @PathVariable("id") String wordId,
                                                @Validated @ModelAttribute WordDTO wordDTO) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        WordResponse res =  wordService.updateWord(requestId, wordId, wordDTO);

        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                        .data(res)
                        .build()
        );
    }

    @DeleteMapping(Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> deleteWord (@RequestParam(value = "req-id", required = false) String requestId,
                                                @PathVariable("id") String wordId) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        wordService.deleteWord(requestId, wordId);

        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                        .data(null)
                        .build()
        );
    }

    @GetMapping(Constants.LESSON + Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> getByLesson (@RequestParam(value = "req-id", required = false) String requestId,
                                                 @PathVariable("id") String lessonId,
                                                 @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                 @RequestParam(value = "size", defaultValue = "10", required = false) int size) {

        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        PageRequest pageRequest = PageRequest.of(
                page, size,
                Sort.by("id").ascending()
        );

        Page<WordResponse> res = wordService.getWordsByLesson(requestId, lessonId, pageRequest);
        return ResponseEntity.ok(
                Response.builder()
                        .data(PagingResponse.<WordResponse>builder()
                                .objects(res.getContent())
                                .totalObjects(res.getTotalElements())
                                .totalPages(res.getTotalPages())
                                .build())
                        .build()
        );
    }

    @GetMapping(Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<Response> getWord (@RequestParam(value = "req-id", required = false) String requestId,
                                             @PathVariable("id") String wordId) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        String languageCodeDes = user.getLanguageCode();

        WordResponse word = wordService.getWord(requestId, wordId, languageCodeDes);
        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                        .data(word)
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }
}
