package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.user.RefreshTokenDTO;
import com.busuu.app.dtos.requests.user.UserActionPasswordDTO;
import com.busuu.app.dtos.requests.user.UserDTO;
import com.busuu.app.dtos.requests.user.UserLoginDTO;
import com.busuu.app.dtos.requests.user.UserUpdateDTO;
import com.busuu.app.dtos.responses.LoginResponse;
import com.busuu.app.dtos.responses.PagingResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.dtos.responses.UserResponse;
import com.busuu.app.entities.Token;
import com.busuu.app.entities.User;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.services.auth.IAuthService;
import com.busuu.app.services.token.ITokenService;
import com.busuu.app.services.user.IUserService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


@RestController
@RequestMapping(Constants.USER)
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final IUserService userService;
    private final ITokenService tokenService;
    private final LocalizationUtils localizationUtils;
    private final IAuthService authService;
    private final ModelMapper modelMapper;

    @GetMapping(Constants.AUTH + Constants.SOCIAL_LOGIN)
    public ResponseEntity<Response> socialAuth (@RequestParam(value = "req-id", required = false) String requestId,
                                         @RequestParam("login_type") String loginType) {
        loginType = loginType.trim().toLowerCase();
        String url = authService.generateAuthUrl(loginType);
        if (url == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Response.builder()
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                        .data(null)
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build()
            );
        }
        return ResponseEntity.status(HttpStatus.OK).body(
                Response.builder()
                        .status(HttpStatus.OK.value())
                        .data(url)
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                        .build()
        );
    }

    @GetMapping(Constants.AUTH + Constants.SOCIAL + Constants.CALLBACK)
    public ResponseEntity<Response> callBack (@RequestParam(value = "req-id", required = false) String requestId,
                                              @RequestParam("code") String code,
                                              @RequestParam("login_type") String loginType,
                                              HttpServletRequest request) throws Exception {
        // Call the AuthService to get user info
        Map<String, Object> userInfo = authService.authenticateAndFetchProfile(code, loginType);

        if (userInfo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Response.builder()
                            .message("Failed to authenticate")
                            .build()
            );
        }

        String accountId = "";
        String name = "";
        String firstName = "";
        String lastName = "";
        String email = "";
        String avatar = "";

        if (loginType.trim().equalsIgnoreCase(Constants.SOCIAL_TYPE.GOOGLE)) {
            accountId = (String) Objects.requireNonNullElse(userInfo.get("sub"), "");
            name = (String) Objects.requireNonNullElse(userInfo.get("name"), "");
            firstName = (String) Objects.requireNonNullElse(userInfo.get("given_name"), "");
            lastName = (String) Objects.requireNonNullElse(userInfo.get("family_name"), "");
            email = (String) Objects.requireNonNullElse(userInfo.get("email"), "");
            avatar = (String) Objects.requireNonNullElse(userInfo.get("picture"), "");
        } else if (loginType.trim().equalsIgnoreCase(Constants.SOCIAL_TYPE.FACEBOOK)) {
            accountId = (String) Objects.requireNonNullElse(userInfo.get("id"), "");
            name = (String) Objects.requireNonNullElse(userInfo.get("name"), "");
            email = (String) Objects.requireNonNullElse(userInfo.get("email"), "");
            Object pictureObj = userInfo.get("picture");
            if (pictureObj instanceof Map) {
                Map<?, ?> pictureData = (Map<?, ?>) pictureObj;
                Object dataObj = pictureData.get("data");
                if (dataObj instanceof Map) {
                    Map<?, ?> dataMap = (Map<?, ?>) dataObj;
                    Object urlObj = dataMap.get("url");
                    if (urlObj instanceof String) {
                        avatar = (String) urlObj;
                    }
                }
            }
        }

        // Create UserDTO
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .email(email)
                .password("")
                .avatar(avatar)
                .fullName(name)
                .firstName(firstName)
                .lastName(lastName)
                .build();


        if (loginType.trim().equalsIgnoreCase(Constants.SOCIAL_TYPE.GOOGLE)) {
            userLoginDTO.setGoogleAccountId(accountId);
        }
        else if (loginType.trim().equalsIgnoreCase(Constants.SOCIAL_TYPE.FACEBOOK)) {
            userLoginDTO.setFacebookAccountId(accountId);
        }

        return loginSocial(userLoginDTO, request);
    }

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
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build()
                );
            }
            UserResponse userResponse = userService.register(requestId, userDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.REGISTER_SUCCESSFULLY))
                            .data(userResponse)
                            .status(HttpStatus.CREATED.value())
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when create user, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.REGISTER_FAILED))
                            .status(HttpStatus.BAD_REQUEST.value())
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
                                .status(HttpStatus.BAD_REQUEST.value())
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
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.LOGIN_FAILED))
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        }
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> getUsers (@RequestParam(value = "req-id", required = false) String requestId,
                                              @RequestParam("role_name") String roleName,

                                              @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                              @RequestParam(value = "size", defaultValue = "10", required = false) int size,

                                              @RequestParam(value = "sort_by", required = false) List<String> sortBy,
                                              @RequestParam(value = "sort_direction", required = false) List<String> sortDirection,
                                              @RequestParam(value = "search_value",required = false) String searchValue,

                                              @RequestParam(value = "is_active",required = false) String isActive)
    {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            Page<UserResponse> users = userService.getUsersByRole(requestId, roleName, page, size, sortBy, sortDirection, searchValue, isActive);

            Object responseData = PagingResponse.<UserResponse>builder()
                    .totalPages(users.getTotalPages())
                    .objects(users.getContent())
                    .totalObjects(users.getTotalElements())
                    .build();

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(responseData)
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.DETAILS)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getDetailUserFromToken (@RequestParam(value = "req-id", required = false) String requestId,
                                                            @RequestHeader("Authorization") String token) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            String extractToken = token.substring(7);

            User user = userService.getUserDetailsFromToken(requestId, extractToken);

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(modelMapper.map(user, UserResponse.class))
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        }
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> updateUser (@RequestParam(value = "req-id", required = false) String requestId,
                                                @ModelAttribute @Valid UserUpdateDTO userUpdateDTO,
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
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build()
                );
            }

            UserResponse res = userService.updateUser(requestId, userUpdateDTO);

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                            .data(res)
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_FAILED))
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @PostMapping(Constants.REFRESH_TOKEN)
    public ResponseEntity<Response> refreshToken(@RequestParam(value = "req-id", required = false) String requestId,
                                                      @Valid @RequestBody RefreshTokenDTO refreshTokenDTO,
                                                      BindingResult result) {

        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                                .build()
                );
            }
            User userDetail = userService.getUserDetailsFromRefreshToken(requestId, refreshTokenDTO.getRefreshToken());
            Token jwtToken = tokenService.refreshToken(refreshTokenDTO.getRefreshToken(), userDetail);

            LoginResponse loginResponse = LoginResponse.builder()
                    .token(jwtToken.getToken())
                    .tokenType(jwtToken.getTokenType())
                    .username(userDetail.getUsername())
                    .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                    .id(userDetail.getId())
                    .refreshToken(jwtToken.getRefreshToken())
                    .build();
            return ResponseEntity.ok(
                    Response.builder()
                            .message("Refresh token successfully")
                            .data(loginResponse)
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        }
    }

    @PutMapping(Constants.CHANGE_PASSWORD)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> changePassword (@RequestParam(value = "req-id", required = false) String requestId,
                                                    @Valid @RequestBody UserActionPasswordDTO userActionPasswordDTO,
                                                    BindingResult result) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.INVALID_ERROR, errorMessages.toString()))
                                .status(HttpStatus.BAD_REQUEST.value())
                                .build()
                );
            }
            boolean isChange = userService.changePassword(requestId, userActionPasswordDTO);
            if (isChange) {
                return ResponseEntity.ok(
                        Response.builder()
                                .data(true)
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.CHANGE_PASSWORD_SUCCESSFULLY))
                                .status(HttpStatus.OK.value())
                                .build()
                );
            }
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .data(false)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.CHANGE_PASSWORD_FAILED))
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        }
    }

    @PutMapping(Constants.GENERATE_OTP)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> generateOTP(@RequestParam(value = "req-id", required = false) String requestId,
                                                @RequestParam("email") String email) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            int isGenerateOTP = userService.generateOTP(requestId, email);
            if (isGenerateOTP == 2) {
                return ResponseEntity.ok(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.OTP_SUCCESSFULLY))
                                .status(HttpStatus.OK.value())
                                .build()
                );
            }
            else  {
                return ResponseEntity.ok(
                        Response.builder()
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_OTP_SUCCESSFULLY))
                                .status(HttpStatus.OK.value())
                                .build()
                );
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        }
    }

    @PutMapping(Constants.CHECK_OTP)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> checkOTP(@RequestParam(value = "req-id", required = false) String requestId,
                                             @RequestParam("email") String email,
                                             @RequestParam("otp") String otp) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            boolean isCheck = userService.checkOTP(requestId, email, otp);
            if (isCheck) {
                return ResponseEntity.ok(
                        Response.builder()
                                .data(true)
                                .status(HttpStatus.OK.value())
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.CHECK_OTP_SUCCESSFULLY))
                                .build()
                );
            }
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .data(false)
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.CHECK_OTP_FAILED))
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        }
    }

    @PutMapping(Constants.BLOCK + Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> blockOrEnable(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String userId) {

        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            User user = userService.blockOrEnable(requestId, userId);
            String message = user.isActive() ? "Successfully enabled the user." : "Successfully blocked the user.";
            return ResponseEntity.ok(
                    Response.builder()
                            .message(message)
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        }
    }


    @GetMapping(Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getUserById (@RequestParam(value = "req-id", required = false) String requestId,
                                                 @PathVariable("id") String userId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            UserResponse userResponse = userService.getUserById(requestId, userId);

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(userResponse)
                            .status(HttpStatus.OK.value())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.EMAIL_UNIQUE)
    public ResponseEntity<Response> emailUnique(@RequestParam(value = "req-id", required = false) String requestId,
                                                @RequestParam("email") String email) {

        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            boolean isUnique = userService.emailUnique(requestId, email);
            if (!isUnique) {
                return ResponseEntity.ok(
                        Response.builder()
                                .status(HttpStatus.OK.value())
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.EMAIL_NOT_EXIST))
                                .data(false)
                                .build()
                );
            }
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.EMAIL_EXIST))
                            .status(HttpStatus.OK.value())
                            .data(true)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.ACTIVE_ACCOUNT)
    public ResponseEntity<?> activeAccount(@RequestParam(value = "req-id", required = false) String requestId,
                                           @RequestParam("email") String email,
                                           @RequestParam("active-code") String activeCode) throws DataNotFoundException {

        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            int isActive = userService.activeAccount(requestId, email, activeCode);
            if (isActive == 1) {
                return ResponseEntity.ok(
                        Response.builder()
                                .data(1)
                                .status(HttpStatus.OK.value())
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.ACTIVATED_ACCOUNT))
                                .build()
                );
            } else if (isActive == 2) {
                return ResponseEntity.ok(
                        Response.builder()
                                .data(2)
                                .status(HttpStatus.OK.value())
                                .message(localizationUtils.getLocalizedMessage(MessagesKey.ACTIVATION_SUCCESSFULLY))
                                .build()
                );
            }
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .data(0)
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.ACTIVATION_FAILED))
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .build()
            );
        }
    }

    private ResponseEntity<Response> loginSocial (@Validated @RequestBody UserLoginDTO userLoginDTO,
                                                  HttpServletRequest request) throws Exception {
        String requestId = UUID.randomUUID().toString();

        String token = userService.loginSocial(userLoginDTO);

        String userAgent = request.getHeader("User-Agent");
        User userDetail = userService.getUserDetailsFromToken(requestId, token);
        Token jwtToken = tokenService.addToken(userDetail, token, isMobileDevice(userAgent));

        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .tokenType(jwtToken.getTokenType())
                .refreshToken(jwtToken.getRefreshToken())
                .username(userDetail.getUsername())
                .roles(userDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .id(userDetail.getId())
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(
                Response.builder()
                        .data(loginResponse)
                        .status(HttpStatus.OK.value())
                        .message(localizationUtils.getLocalizedMessage(MessagesKey.LOGIN_SUCCESSFULLY))
                        .build()
        );
    }

    private boolean isMobileDevice(String userAgent) {
        return userAgent.toLowerCase().contains("mobile");
    }
}
