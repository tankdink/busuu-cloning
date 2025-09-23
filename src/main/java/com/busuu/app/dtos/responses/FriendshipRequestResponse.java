package com.busuu.app.dtos.responses;

import com.busuu.app.entities.User;
import com.busuu.app.entities.notifications.NotificationStatus;
import com.busuu.app.entities.notifications.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendshipRequestResponse extends BaseResponse
{
    @JsonProperty("user_id")
    private String id;

}
