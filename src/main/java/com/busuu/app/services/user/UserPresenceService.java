package com.busuu.app.services.user;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.PresenceFriendResponse;
import com.busuu.app.entities.User;
import com.busuu.app.entities.enums.FriendShipStatus;
import com.busuu.app.entities.enums.PresenceStatus;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.UserRepository;
import com.busuu.app.services.publisher.PresenceEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBatch;
import org.redisson.api.RFuture;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPresenceService {

    private final RedissonClient redissonClient;

    private final UserRepository userRepository;

    private final PresenceEventPublisher presenceEventPublisher;

    private static final long ONLINE_TTL_SECONDS = 60;

    private static final String PRESENCE_KEY = "user:presence:%s";

    public void setOnline(String requestId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            redissonClient.getBucket(buildKey(userId), StringCodec.INSTANCE)
                    .set("1", ONLINE_TTL_SECONDS, TimeUnit.SECONDS);

            //  Publish event for friends
            List<String> friends = userRepository.findFriendIds(userId, FriendShipStatus.ACCEPT);
            presenceEventPublisher.publishPresenceChange(userId, PresenceStatus.ONLINE, friends);
        } catch (Exception e) {
            log.error("requestId={},failed to update presence status of user, err={}", requestId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_PRESENCE_STATUS, requestId);
        }

    }

    public void refreshPresence(String requestId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            redissonClient.getBucket(buildKey(userId), StringCodec.INSTANCE)
                    .set("1", ONLINE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("requestId={},failed to update presence status of user, err={}", requestId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_PRESENCE_STATUS, requestId);
        }
    }


    public boolean isOnline(String requestId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            return redissonClient.getBucket(buildKey(userId), StringCodec.INSTANCE).isExists();
        } catch (Exception e){
            log.error("requestId={},failed to get presence status of user, err={}", requestId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_PRESENCE_STATUS, requestId);
        }
    }

    public List<PresenceFriendResponse> getFriendsStatus(String requestId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            List<String> friendIds = userRepository.findFriendIds(userId, FriendShipStatus.ACCEPT);
            if (friendIds.isEmpty()) {
                return List.of();
            }

            // Use pipeline (batch)
            RBatch batch = redissonClient.createBatch();
            Map<String, RFuture<Object>> futures = new HashMap<>();

            for (String fid : friendIds) {
                String key = buildKey(fid);
                futures.put(fid, batch.getBucket(key, StringCodec.INSTANCE).getAsync());
            }

            batch.execute();

            List<PresenceFriendResponse> result = new ArrayList<>();
            for (String fid : friendIds) {
                Object value = futures.get(fid).getNow();
                PresenceStatus status = (value != null) ? PresenceStatus.ONLINE : PresenceStatus.OFFLINE;

                result.add(PresenceFriendResponse.builder()
                        .userId(fid)
                        .status(status)
                        .build());
            }

            return result;
        } catch (Exception e) {
            log.error("requestId={}, failed to get presence status of friends, err={}", requestId, e.getMessage(), e);
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_PRESENCE_STATUS, requestId);
        }
    }

    private String buildKey(String userId) {
        return String.format(PRESENCE_KEY, userId);
    }
}
