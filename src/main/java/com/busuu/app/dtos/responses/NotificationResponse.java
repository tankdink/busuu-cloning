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
public class NotificationResponse extends BaseResponse
{
    @JsonProperty("id")
    private String id;

    @JsonProperty("destination_id")
    private String destinationId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private NotificationStatus status;

    @JsonProperty("type")
    private NotificationType type;
}
