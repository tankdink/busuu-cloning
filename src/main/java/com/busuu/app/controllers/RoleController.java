package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.role.RoleDTO;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.entities.Role;
import com.busuu.app.services.role.IRoleService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.ROLE)
@RequiredArgsConstructor
@Slf4j
public class RoleController {
    private final IRoleService roleService;
    private final LocalizationUtils localizationUtils;

    @PostMapping()
    public ResponseEntity<Response> insertRole (@RequestParam(value = "req-id", required = false) String requestId,
                                                @Valid @RequestBody RoleDTO roleDTO,
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
            Role role = roleService.insertRole(requestId, roleDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .data(role)
                            .status(HttpStatus.CREATED)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when create role, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping()
    public ResponseEntity<Response> getRoles (@RequestParam(value = "req-id", required = false) String requestId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            List<Role> roles = roleService.getRoles(requestId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(roles)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get roles, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping(Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> getRole (@RequestParam(value = "req-id", required = false) String requestId,
                                             @PathVariable("id") String roleId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            Role role = roleService.getRole(requestId, roleId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(role)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get role, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @PutMapping(Constants.PATH_PARAM_ID)
    public ResponseEntity<?> updateRole (@RequestParam(value = "req-id", required = false) String requestId,
                                         @PathVariable("id") String roleId,
                                         @Valid @RequestBody RoleDTO roleDTO,
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

            Role role = roleService.updateRole(requestId, roleId, roleDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                            .data(role)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when update role, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_FAILED))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @DeleteMapping(Constants.PATH_PARAM_ID)
    public ResponseEntity<Response> deleteRole (@RequestParam(value = "req-id", required = false) String requestId,
                                                @PathVariable("id") String roleId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            roleService.deleteRole(requestId, roleId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when delete role, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED))
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }
}
