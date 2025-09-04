package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.FriendResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.services.friend.IFriendService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Controller
@RestController
@RequestMapping(Constants.FRIEND)
@RequiredArgsConstructor
@Slf4j
public class FriendController
{
    private final IFriendService friendService;

    private final LocalizationUtils localizationUtils;

    @GetMapping()
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getFriends(@RequestParam(value = "req-id", required = false) String requestId,

                                                @RequestParam(value = "sort-by", required = false) List<String> sortBy,
                                                @RequestParam(value = "sort-dir", required = false) List<String> sortDirection,
                                                @RequestParam(value = "search-value",required = false) String searchValue,

                                                @RequestParam(value = "country",required = false) String country)
    {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            FriendResponse response = friendService.getFriends(requestId, sortBy, sortDirection, searchValue, country);

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(response)
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get friend list, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }


    @DeleteMapping(value = Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> deleteFriend(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String userId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            friendService.deleteFriend(requestId, userId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when delete friend, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

}
