package com.busuu.app.listeners;

import com.busuu.app.services.user.UserPresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class PresenceEventListener {

    private final UserPresenceService userPresenceService;

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
        Principal principal = event.getUser();
        if (principal == null) {
            log.warn("SessionConnectedEvent without principal, sessionId={}", sessionId);
            return;
        }
        String userId = principal.getName();

        try {
            userPresenceService.setOnline("CONNECT-" + sessionId, sessionId, userId);
            log.info("User {} connected with sessionId={}", userId, sessionId);
        } catch (Exception e) {
            log.error("Failed to handle connect for userId={}, sessionId={}, err={}", userId, sessionId, e.getMessage(), e);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        Principal principal = event.getUser();
        if (principal == null) {
            log.warn("SessionDisconnectEvent without principal, sessionId={}", sessionId);
            return;
        }
        String userId = principal.getName();

        try {
            userPresenceService.setOffline("DISCONNECT-" + sessionId, sessionId, userId);
            log.info("User {} disconnected with sessionId={}", userId, sessionId);
        } catch (Exception e) {
            log.error("Failed to handle disconnect for userId={}, sessionId={}, err={}", userId, sessionId, e.getMessage(), e);
        }
    }
}