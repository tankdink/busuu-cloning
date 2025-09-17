package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.course.CourseDTO;
import com.busuu.app.dtos.responses.*;
import com.busuu.app.services.course.ICourseService;
import com.busuu.app.services.notification.INotificationService;
import com.busuu.app.services.userLanguage.IUserLanguageService;
import com.busuu.app.services.userLanguage.UserLanguageService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RestController
@RequestMapping(Constants.NOTIFICATION)
@RequiredArgsConstructor
@Slf4j
public class NotificationController
{

    private final LocalizationUtils localizationUtils;

    private final INotificationService notificationService;

    @GetMapping()
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> getNotifications(@RequestParam(value = "req-id", required = false) String requestId,

                                                    @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                    @RequestParam(value = "size", defaultValue = "10", required = false) int size)
    {

        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            Page<NotificationResponse> notifications = notificationService.getSelfNotification(requestId, page, size);
            Object responseData = PagingResponse.<NotificationResponse>builder()
                    .totalPages(notifications.getTotalPages())
                    .objects(notifications.getContent())
                    .totalObjects(notificationService.countUnreadNotifications())
                    .build();

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(responseData)
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get notifications, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> deleteNotification(@RequestParam(value = "req-id", required = false) String requestId,
                                                       @PathVariable(value = "id") String notificationId)
    {

        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            notificationService.deleteNotification(requestId, notificationId);

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(null)
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when delete notifications, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @PutMapping(value = Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Response> changeStatus(@RequestParam(value = "req-id", required = false) String requestId,
                                                 @PathVariable("id") String notificationId)
    {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            NotificationResponse response = notificationService.changeStatus(requestId, notificationId);

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                            .data(response)
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when changing status, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

}
