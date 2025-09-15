package com.busuu.app.dtos.responses;

import com.busuu.app.entities.enums.PresenceStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PresenceFriendResponse {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("status")
    private PresenceStatus status;

    @JsonProperty("last_seen_at")
    private Instant lastSeenAt;
}
