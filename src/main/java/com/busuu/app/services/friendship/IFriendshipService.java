package com.busuu.app.services.friendship;


import com.busuu.app.dtos.responses.FriendshipResponse;

import java.util.List;

public interface IFriendshipService
{

    String addFriendRequest(String requestId, String userId);

    String respondRequest(String requestId, String userId, String respond);

    FriendshipResponse getFriends(String requestId, List<String> sortBy, List<String> sortDirection, String searchValue, String country);

    FriendshipResponse getFriendsByUserId(String userId);

    FriendshipResponse getPendingRequest(String requestId);

    List<String> getRandomList(String requestId);

    String getFriendshipStatus(String requestId, String userId);

    void deleteFriend(String requestId, String friendId);


}

