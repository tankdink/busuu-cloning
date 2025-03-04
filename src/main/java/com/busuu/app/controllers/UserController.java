package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.user.UserDTO;
import com.busuu.app.dtos.requests.user.UserLoginDTO;
import com.busuu.app.dtos.responses.LoginResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.dtos.responses.UserResponse;
import com.busuu.app.entities.Token;
import com.busuu.app.entities.User;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.services.token.ITokenService;
import com.busuu.app.services.user.IUserService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.USER)
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final IUserService userService;
    private final ITokenService tokenService;
    private final LocalizationUtils localizationUtils;

    @PostMapping(value = Constants.REGISTER)
    public ResponseEntity<Response> register (@RequestParam(value = "req-id", required = false) String requestId,
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
            UserResponse userResponse = userService.register(requestId, userDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.REGISTER_SUCCESSFULLY))
                            .data(userResponse)
                            .status(HttpStatus.CREATED)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when create user, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.REGISTER_FAILED))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @PostMapping(value = Constants.LOGIN)
    public ResponseEntity<Response> login (HttpServletRequest request,
                                           @RequestParam(value = "req-id", required = false) String requestId,
                                           @Valid @RequestBody UserLoginDTO userLoginDTO,
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

            String token = userService.login(requestId, userLoginDTO.getEmail(), userLoginDTO.getPassword());
            String userAgent = request.getHeader("User-Agent");
            User user = userService.getUserDetailsFromToken(requestId, token);
            Token jwtToken = tokenService.addToken(user, token, userAgent.toLowerCase().contains("mobile"));

            LoginResponse loginResponse = LoginResponse.builder()
                    .id(user.getId())
                    .token(token)
                    .tokenType(jwtToken.getTokenType())
                    .refreshToken(jwtToken.getRefreshToken())
                    .username(user.getUsername())
                    .roles(user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .build();

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.LOGIN_SUCCESSFULLY))
                            .data(loginResponse)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.LOGIN_FAILED))
                            .status(HttpStatus.UNAUTHORIZED)
                            .build()
            );
        }
    }

}
