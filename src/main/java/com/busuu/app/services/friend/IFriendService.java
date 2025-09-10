package com.busuu.app.services.friend;


import com.busuu.app.dtos.requests.course.CourseDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.CourseResponse;
import com.busuu.app.dtos.responses.FriendResponse;
import com.busuu.app.dtos.responses.UserLanguageResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFriendService
{

    void addFriend(String requestId, String userId);

    FriendResponse getFriends(String requestId, List<String> sortBy, List<String> sortDirection, String searchValue, String country);

    FriendResponse getFriendsByUserId(String userId);

    List<String> getRandomList(String requestId);

    void deleteFriend(String requestId, String friendId);


}

