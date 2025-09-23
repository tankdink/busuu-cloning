package com.busuu.app.services.publisher;

import com.busuu.app.dtos.responses.FriendshipRequestResponse;
import com.busuu.app.dtos.responses.NotificationResponse;
import com.busuu.app.dtos.responses.PresenceFriendResponse;
import com.busuu.app.dtos.socket.SocketPayload;
import com.busuu.app.entities.User;
import com.busuu.app.entities.enums.PresenceStatus;
import com.busuu.app.entities.enums.TopicSocket;
import com.busuu.app.entities.enums.TypeSocket;
import com.busuu.app.entities.notifications.Notification;
import com.busuu.app.entities.notifications.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendRequestEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    // Send events
    public void publishFriendRequest(String toUser)
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User userActor = (User) auth.getPrincipal();

        SocketPayload<FriendshipRequestResponse> payload = SocketPayload.<FriendshipRequestResponse>builder()
                .messageId(UUID.randomUUID().toString())
                .data(FriendshipRequestResponse.builder().id(userActor.getId()).build())
                .timestamp(Instant.now())
                .type(TypeSocket.REQUEST)
                .build();

        payload.getData().setCreatedAt(LocalDateTime.now());

        String destination = "/topic/" + TopicSocket.REQUEST.getValue() + "." + toUser;
        messagingTemplate.convertAndSend(destination, payload);


        log.info("Publishing [{}] notification event from user={} to user={}",
                payload.getType(), userActor.getId(), toUser);
    }
}
