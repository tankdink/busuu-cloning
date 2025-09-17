package com.busuu.app.controllers.socket;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.services.user.UserPresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserPresenceSocket {

    private final UserPresenceService userPresenceService;

    @MessageMapping(Constants.PRESENCE + Constants.SOCKET_DESTINATION.HEARTBEAT)
    public void heartbeat(Message<?> message, Principal principal) {
        String requestId = UUID.randomUUID().toString();

        if (principal == null) return;

        String userId = principal.getName();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();

        userPresenceService.refreshPresence(requestId, sessionId, userId);
        log.info("Presence heartbeat received for userId={}, sessionId={}", userId, sessionId);
    }
}
