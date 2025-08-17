package com.busuu.app.controllers;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.post.PostDTO;
import com.busuu.app.dtos.responses.PagingResponse;
import com.busuu.app.dtos.responses.PostResponse;
import com.busuu.app.dtos.responses.Response;
import com.busuu.app.services.post.IPostService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(Constants.POST)
@RequiredArgsConstructor
@Slf4j
public class PostController
{
    private final IPostService postService;

    private final LocalizationUtils localizationUtils;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> postPost(@RequestParam(value = "req-id", required = false) String requestId,
                                                  @Valid @ModelAttribute PostDTO newPostDTO)
    {
        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call add post service
            PostResponse addedPost = postService.insertPost(requestId, newPostDTO);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_SUCCESSFULLY))
                            .status(HttpStatus.CREATED.value())
                            .data(addedPost)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when adding new post: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.INSERT_DATA_FAILED) +": "+ e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping()
    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getPosts(@RequestParam(value = "req-id", required = false) String requestId,

                                              @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                              @RequestParam(value = "size", defaultValue = "10", required = false) int size,

                                              @RequestParam(value = "sort_by", required = false) List<String> sortBy,
                                              @RequestParam(value = "sort_direction", required = false) List<String> sortDirection,
                                              @RequestParam(value = "search_value", required = false) String searchValue,
                                              @RequestParam(value = "filter_by", required = false) List<String> filterBy,
                                              @RequestParam(value = "filter_value", required = false) List<String> filterValue) {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call update chapter by ID service
            Page<PostResponse> postsList = postService.getPosts(requestId, page, size, sortBy, sortDirection, searchValue, filterBy, filterValue);
            Object responseData = PagingResponse.<PostResponse>builder()
                    .totalPages(postsList.getTotalPages())
                    .objects(postsList.getContent())
                    .totalObjects(postsList.getTotalElements())
                    .build();

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .data(responseData)
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting posts list: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) + ": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getPost(@RequestParam(value = "req-id", required = false) String requestId,
                                               @PathVariable("id") String postId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get chapter by ID service
            PostResponse post = postService.getPost(requestId, postId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .data(post)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting post with ID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

    @GetMapping(Constants.PATH_PARAM_USER_POST + Constants.PATH_PARAM_ID)
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    public ResponseEntity<Response> getPostByUserId(@RequestParam(value = "req-id", required = false) String requestId,
                                            @PathVariable("id") String userId)
    {

        try {

            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            //Call get chapter by ID service
            List<PostResponse> postList = postService.getByUserId(requestId, userId);

            //Return response
            return ResponseEntity.ok().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_SUCCESSFULLY))
                            .status(HttpStatus.OK.value())
                            .data(postList)
                            .build()
            );

        } catch (Exception e) {
            log.error("Error when getting post with userID: " + e.getMessage());
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(localizationUtils.getLocalizedMessage(MessagesKey.GET_DATA_FAILED) +": " + e.getMessage())
                            .status(HttpStatus.BAD_REQUEST.value())
                            .build()
            );
        }
    }

}
