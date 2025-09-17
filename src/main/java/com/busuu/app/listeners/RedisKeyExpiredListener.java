package com.busuu.app.listeners;

import com.busuu.app.entities.enums.FriendshipStatus;
import com.busuu.app.entities.enums.PresenceStatus;
import com.busuu.app.repositories.UserRepository;
import com.busuu.app.services.publisher.PresenceEventPublisher;
import com.busuu.app.services.user.UserPresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RedisKeyExpiredListener {

    private final UserRepository userRepository;
    private final RedissonClient redissonClient;

    private final UserPresenceService userPresenceService;

    private final PresenceEventPublisher presenceEventPublisher;

    @Bean
    public MessageListener<String> listenKeyExpired() {
        MessageListener<String> listener = (channel, msg) -> {
            log.info("Expired event received: channel={}, msg={}", channel, msg);

            if (msg.startsWith("user:presence:")) {
                String[] parts = msg.split(":");
                if (parts.length < 4) {
                    log.warn("Invalid expired key format: {}", msg);
                    return;
                }
                String userId = parts[2];
                String sessionId = parts[3];

                log.info("User {} offline detected via key expiration, sessionId={}", userId, sessionId);

                String requestId = UUID.randomUUID().toString();

                boolean stillOnline = userPresenceService.isOnline(requestId, userId);
                if (!stillOnline) {
                    userRepository.updateLastSeenAt(userId, Instant.now());
                    List<String> friends = userRepository.findFriendIds(userId, FriendshipStatus.ACCEPT);
                    presenceEventPublisher.publishPresenceChange(userId, PresenceStatus.OFFLINE, friends);
                } else {
                    log.info("User {} still has active sessions, skip OFFLINE event", userId);
                }
            }
        };

        // Register listener with Redis topic
        redissonClient.getTopic("__keyevent@0__:expired", StringCodec.INSTANCE)
                .addListener(String.class, listener);

        return listener;
    }


}