package com.busuu.app.services.publisher;

import com.busuu.app.dtos.responses.PresenceFriendResponse;
import com.busuu.app.dtos.socket.SocketPayload;
import com.busuu.app.entities.enums.PresenceStatus;
import com.busuu.app.entities.enums.TopicSocket;
import com.busuu.app.entities.enums.TypeSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    // Send events
    public void publishPresenceChange(String userId, PresenceStatus status, List<String> friendIds) {
        SocketPayload<PresenceFriendResponse> payload = SocketPayload.<PresenceFriendResponse>builder()
                .messageId(UUID.randomUUID().toString())
                .data(PresenceFriendResponse.builder()
                        .userId(userId)
                        .status(status)
                        .lastSeenAt(Instant.now())
                        .build())
                .timestamp(Instant.now())
                .type(TypeSocket.PRESENCE_CHANGE)
                .build();

        for (String fid : friendIds) {
            String destination = "/topic/" + TopicSocket.PRESENCE.getValue() + "." + fid;
            messagingTemplate.convertAndSend(destination, payload);
        }

        log.info("Publishing [{}] event for user={} to friends={}",
                payload.getType(), userId, friendIds);
    }
}
