package com.busuu.app.controllers;


import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.PresenceFriendResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.dtos.responses.UserInfoResponse;
import com.busuu.app.services.user.UserPresenceService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.PRESENCE)
@RequiredArgsConstructor
@Slf4j
public class UserPresenceController {

    private final UserPresenceService userPresenceService;

    private final LocalizationUtils localizationUtils;


    @PutMapping()
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> setOnline (@RequestParam(value = "req-id", required = false) String requestId) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        userPresenceService.setOnline(requestId, requestId, null);

        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                        .data("Set presence of user successfully")
                        .status(HttpStatus.CREATED.value())
                        .build());
    }

    @GetMapping()
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> getPresence (@RequestParam(value = "req-id", required = false) String requestId) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        boolean isOnline = userPresenceService.isOnline(requestId, null);

        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                        .data(isOnline)
                        .status(HttpStatus.CREATED.value())
                        .build());
    }

    @PutMapping(Constants.HEARTBEAT)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> heartbeat (@RequestParam(value = "req-id", required = false) String requestId) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        userPresenceService.refreshPresence(requestId, requestId, null);

        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                        .data("Refresh presence of user successfully")
                        .status(HttpStatus.CREATED.value())
                        .build());
    }

    @GetMapping(Constants.FRIENDS)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> getPresenceFriends (@RequestParam(value = "req-id", required = false) String requestId) {
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }

        List<UserInfoResponse> res = userPresenceService.getFriendsStatus(requestId);

        return ResponseEntity.ok(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                        .data(res)
                        .status(HttpStatus.CREATED.value())
                        .build());
    }

}
