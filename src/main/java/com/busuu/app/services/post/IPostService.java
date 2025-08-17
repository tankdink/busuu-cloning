package com.busuu.app.services.post;

import com.busuu.app.dtos.requests.post.PostDTO;
import com.busuu.app.dtos.responses.PostResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPostService
{

    PostResponse insertPost(String requestId, PostDTO postDTO);

    Page<PostResponse> getPosts(String requestId, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, List<String> filterBy, List<String> filterValue);

    PostResponse getPost(String requestId, String postId);

   List<PostResponse> getByUserId(String requestId, String userId);

}
