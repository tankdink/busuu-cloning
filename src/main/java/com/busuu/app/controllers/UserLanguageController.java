package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.user.UserLanguageDTO;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.dtos.responses.UserLanguageResponse;
import com.busuu.app.services.userLanguage.UserLanguageService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.USER_LANGUAGE)
@RequiredArgsConstructor
@Slf4j
public class UserLanguageController {

    private final LocalizationUtils localizationUtils;

    private final UserLanguageService userLanguageService;

    @PostMapping()
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<Response> upsertUserLanguage (@RequestParam(value = "req-id", required = false) String requestId,
                                                        @Valid @RequestBody UserLanguageDTO userLanguageDTO) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        UserLanguageResponse res = userLanguageService.upSertUserLanguage(requestId, userLanguageDTO);

        return ResponseEntity.ok(
                Response.builder()
                        .data(res)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<Response> getUserLanguage (@RequestParam(value = "req-id", required = false) String requestId,
                                                     @RequestParam(value = "is-learning", required = false) Boolean isLearning) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        Object res;

        if (isLearning == null)  res = userLanguageService.getUserLanguages(requestId);
        else res = userLanguageService.getLearningLanguage(requestId, isLearning);

        return ResponseEntity.ok(
                Response.builder()
                        .data(res)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }

    @GetMapping(Constants.USER + Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<Response> getUserLanguages (@RequestParam(value = "req-id", required = false) String requestId,
                                                      @PathVariable("id") String userId) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        List<UserLanguageResponse> res = userLanguageService.getUserLanguages(requestId, userId);

        return ResponseEntity.ok(
                Response.builder()
                        .data(res)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                        .status(HttpStatus.OK.value())
                        .build()
        );
    }
}
