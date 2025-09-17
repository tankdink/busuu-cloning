package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.FriendshipResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.services.friendship.IFriendshipService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RestController
@RequestMapping(Constants.FRIENDSHIP)
@RequiredArgsConstructor
@Slf4j
public class FriendshipController
{
    private final IFriendshipService friendService;

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

            FriendshipResponse response = friendService.getFriends(requestId, sortBy, sortDirection, searchValue, country);

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

    @GetMapping(Constants.RANDOM)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> getRandomList(@RequestParam(value = "req-id", required = false) String requestId)
    {

        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            List<String> response = friendService.getRandomList(requestId);

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(response)
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get user list, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.STATS + Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getFriendshipStatus(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String userId)
    {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            String response = friendService.getFriendshipStatus(requestId, userId);

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(response)
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get friend status, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.PENDING)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getPendingRequest(@RequestParam(value = "req-id", required = false) String requestId)
    {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            FriendshipResponse response = friendService.getPendingRequest(requestId);

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(response)
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get pending request, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @PostMapping(Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> addOrRespondFriendshipRequest(@RequestParam(value = "req-id", required = false) String requestId,
                                                                  @PathVariable("id") String userId,
                                                                  @RequestParam(value = "respond", required = false) String respond) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }
            String response = null;

            if (respond == null || respond.isEmpty()) response = friendService.addFriendRequest(requestId, userId);
            else response = friendService.respondRequest(requestId, userId, respond);


            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(response)
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when add friend request, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }


}
