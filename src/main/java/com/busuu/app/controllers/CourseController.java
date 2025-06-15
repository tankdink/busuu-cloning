package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.course.CourseDTO;
import com.busuu.app.dtos.responses.CourseResponse;
import com.busuu.app.dtos.responses.PagingResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.services.course.ICourseService;
import com.busuu.app.utils.LocalizationUtils;
import com.busuu.app.utils.MessagesKey;
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
@RequestMapping(Constants.COURSE)
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final ICourseService courseService;
    private final LocalizationUtils localizationUtils;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> insertCourse (@RequestParam(value = "req-id", required = false) String requestId,
                                                  @Valid @ModelAttribute CourseDTO courseDTO,
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
            CourseResponse course = courseService.insertCourse(requestId, courseDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .data(course)
                            .status(HttpStatus.CREATED)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when create course, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getCourses (@RequestParam(value = "req-id", required = false) String requestId,

                                                @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                @RequestParam(value = "sort_by", defaultValue = "courseOrder", required = false) String sortBy,
                                                @RequestParam(value = "sort_direction", defaultValue = "ASC", required = false) String sortDirection)
    {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            Page<CourseResponse> courses = courseService.getCourses(requestId, page, size, sortBy, sortDirection);
            Object responseData = PagingResponse.<CourseResponse>builder()
                    .totalPages(courses.getTotalPages())
                    .objects(courses.getContent())
                    .totalObjects(courses.getTotalElements())
                    .build();

            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(responseData)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get courses, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @GetMapping(value = Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getCourse (@RequestParam(value = "req-id", required = false) String requestId,
                                                @PathVariable("id") String courseId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            CourseResponse course = courseService.getCourse(requestId, courseId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .data(course)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when get course, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @PutMapping(value = Constants.PATH_PARAM_ID, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Response> updateCourse (@RequestParam(value = "req-id", required = false) String requestId,
                                                  @PathVariable("id") String courseId,
                                                  @Valid @ModelAttribute CourseDTO courseDTO,
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

            CourseResponse course = courseService.updateCourse(requestId, courseId, courseDTO);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_SUCCESSFULLY))
                            .data(course)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when update course, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.UPDATE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }

    @DeleteMapping(value = Constants.PATH_PARAM_ID)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Response> deleteCourse (@RequestParam(value = "req-id", required = false) String requestId,
                                               @PathVariable("id") String courseId) {
        try {
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            courseService.deleteCourse(requestId, courseId);
            return ResponseEntity.ok(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error when delete course, " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.DELETE_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
            );
        }
    }
}
