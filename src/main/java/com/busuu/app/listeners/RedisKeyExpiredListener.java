package com.busuu.app.listeners;

import com.busuu.app.entities.enums.FriendshipStatus;
import com.busuu.app.entities.enums.PresenceStatus;
import com.busuu.app.repositories.UserRepository;
import com.busuu.app.services.publisher.PresenceEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.List;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RedisKeyExpiredListener {

    private final UserRepository userRepository;
    private final RedissonClient redissonClient;

    private final PresenceEventPublisher presenceEventPublisher;

    @Bean
    public MessageListener<String> listenKeyExpired() {
        MessageListener<String> listener = (channel, msg) -> {
            log.info("Expired event received: channel={}, msg={}", channel, msg);

            if (msg.startsWith("user:presence:")) {
                String userId = msg.replace("user:presence:", "");
                log.info("User {} offline detected via key expiration", userId);
                userRepository.updateLastSeenAt(userId, Instant.now());

                List<String> friends = userRepository.findFriendIds(userId, FriendshipStatus.ACCEPT);

                //  Publish event for friends
                presenceEventPublisher.publishPresenceChange(userId, PresenceStatus.OFFLINE, friends);
            }
        };

        // Register listener with Redis topic
        redissonClient.getTopic("__keyevent@0__:expired", StringCodec.INSTANCE)
                .addListener(String.class, listener);

        return listener;
    }

}