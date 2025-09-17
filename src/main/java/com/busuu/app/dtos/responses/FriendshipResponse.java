package com.busuu.app.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendshipResponse extends BaseResponse
{

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("friend_ids")
    private List<String> friendIds;

}
