package com.busuu.app.configs.security;

import com.busuu.app.entities.enums.TopicSocket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            Principal principal = accessor.getUser();
            String userId = principal.getName();

            if (destination == null || userId == null) {
                log.warn("Missing destination or userId in SUBSCRIBE");
                throw new AccessDeniedException("Missing destination or userId in SUBSCRIBE");
            }

            // parse userId from topic
            String[] parts = destination.split("\\.");
            if (parts.length < 2) {
                log.warn("Invalid topic format: {}", destination);
                throw new AccessDeniedException("Invalid topic format");
            }
            String topicUserId = parts[parts.length - 1];

            // validate both session userId and topic userId
            if (!TopicSocket.isValidDestination(destination)) {
                log.warn("Invalid topic destination: sessionUserId={}, destination={}", userId, destination);
                throw new AccessDeniedException("Invalid subscription destination");
            }

            if (!topicUserId.equals(userId)) {
                log.warn("Forbidden subscription attempt: sessionUserId={}, topicUserId={}, destination={}",
                        userId, topicUserId, destination);
                throw new AccessDeniedException("Not allowed to subscribe to another user's topic");
            }

            log.info("Valid SUBSCRIBE: userId={}, destination={}", userId, destination);
        }

        return message;
    }
}
