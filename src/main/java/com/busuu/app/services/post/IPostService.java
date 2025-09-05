package com.busuu.app.services.post;

import com.busuu.app.dtos.requests.post.PostDTO;
import com.busuu.app.dtos.responses.PostResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPostService
{

    PostResponse insertPost(String requestId, PostDTO postDTO);

    Page<PostResponse> getPosts(String requestId, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, String postType, String country);

    PostResponse getPost(String requestId, String postId);

    Page<PostResponse> getSelfPost(String requestId, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, String postType, String country);

    List<PostResponse> getByUserId(String requestId, String userId);

    Page<PostResponse> getByFriendlist(String requestId, int page, int size);

}
