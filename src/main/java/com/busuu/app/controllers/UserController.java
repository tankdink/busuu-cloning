package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.user.UserDTO;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.services.user.IUserService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Controller
@RestController(Constants.USER)
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final IUserService userService;
    private final LocalizationUtils localizationUtils;

    @PostMapping()
    public ResponseEntity<Response> createUser (@RequestParam(value = "req-id", required = false) String requestId,
                                                @Valid @RequestBody UserDTO userDTO,
                                                BindingResult result) {
        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();

                // Log error
                log.error(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()));

                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                                .status(HttpStatus.BAD_REQUEST)
                                .build()
                );
            }

            return null;
        } catch (Exception e) {
            log.error("Error when create user, " + e.getMessage());
            throw new ErrorHandleException("Error when create user, " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_USER, requestId);
        }
    }

}
