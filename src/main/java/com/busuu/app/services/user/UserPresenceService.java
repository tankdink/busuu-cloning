package com.busuu.app.services.user;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.PresenceFriendResponse;
import com.busuu.app.dtos.responses.UserInfoResponse;
import com.busuu.app.entities.User;
import com.busuu.app.entities.enums.FriendshipStatus;
import com.busuu.app.entities.enums.PresenceStatus;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.UserRepository;
import com.busuu.app.services.publisher.PresenceEventPublisher;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserPresenceService {

    private final RedissonClient redissonClient;

    private final UserRepository userRepository;

    private final PresenceEventPublisher presenceEventPublisher;

    private static final long ONLINE_TTL_SECONDS = 60;

    private static final String PRESENCE_KEY = "user:presence:%s:%s";

    private static final String PRESENCE_PATTERN = "user:presence:%s:*";

    private final ModelMapper modelMapper;

    public void setOnline(String requestId, String sessionId, String userId) {
        try {
            if (Strings.isNullOrEmpty(userId)) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User user = (User) auth.getPrincipal();
                userId = user.getId();
            }

            String key = buildKey(userId, sessionId);

            boolean wasOffline = !isOnline(requestId, userId);
            redissonClient.getBucket(key, StringCodec.INSTANCE)
                    .set("1", ONLINE_TTL_SECONDS, TimeUnit.SECONDS);

            if (wasOffline) {
                List<String> friends = userRepository.findFriendIds(userId, FriendshipStatus.ACCEPT);
                presenceEventPublisher.publishPresenceChange(userId, PresenceStatus.ONLINE, friends);
            }
        } catch (Exception e) {
            log.error("requestId={},failed to update presence status of user, err={}", requestId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_PRESENCE_STATUS, requestId);
        }

    }

    public void setOffline(String requestId, String sessionId, String userId) {
        try {
            if (Strings.isNullOrEmpty(userId)) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User user = (User) auth.getPrincipal();
                userId = user.getId();
            }

            String key = buildKey(userId, sessionId);
            redissonClient.getBucket(key, StringCodec.INSTANCE).delete();

            if (!isOnline(requestId, userId)) {
                List<String> friends = userRepository.findFriendIds(userId, FriendshipStatus.ACCEPT);
                presenceEventPublisher.publishPresenceChange(userId, PresenceStatus.OFFLINE, friends);
            }
        } catch (Exception e) {
            log.error("requestId={},failed to update presence status of user, err={}", requestId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_PRESENCE_STATUS, requestId);
        }
    }

    public void refreshPresence(String requestId, String sessionId, String userId) {
        try {
            if (Strings.isNullOrEmpty(userId)) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User user = (User) auth.getPrincipal();
                userId = user.getId();
            }

            redissonClient.getBucket(buildKey(userId, sessionId), StringCodec.INSTANCE)
                    .set("1", ONLINE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("requestId={},failed to update presence status of user, err={}", requestId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_PRESENCE_STATUS, requestId);
        }
    }


    public boolean isOnline(String requestId, String userId) {
        try {
            if (Strings.isNullOrEmpty(userId)) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User user = (User) auth.getPrincipal();
                userId = user.getId();
            }

            RKeys keys = redissonClient.getKeys();
            Iterable<String> iter = keys.getKeysByPattern("user:presence:" + userId + ":*");
            return iter.iterator().hasNext();
        } catch (Exception e){
            log.error("requestId={},failed to get presence status of user, err={}", requestId, e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_PRESENCE_STATUS, requestId);
        }
    }

    public List<UserInfoResponse> getFriendsStatus(String requestId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            List<String> friendIds = userRepository.findFriendIds(userId, FriendshipStatus.ACCEPT);
            if (friendIds.isEmpty()) {
                return List.of();
            }

            List<UserInfoResponse> result = new ArrayList<>();
            RKeys rKeys = redissonClient.getKeys();

            for (String fid : friendIds) {
                String pattern = buildPattern(fid);
                boolean isOnline = redissonClient.getKeys().getKeysByPattern(pattern, 1).iterator().hasNext();

                User friendDetail = userRepository.findById(fid)
                        .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + fid));

                UserInfoResponse userInfoResponse = modelMapper.map(friendDetail, UserInfoResponse.class);
                userInfoResponse.setStatus(isOnline ? PresenceStatus.ONLINE : PresenceStatus.OFFLINE);
                userInfoResponse.setLastSeenAt(friendDetail.getLastSeenAt());
                result.add(userInfoResponse);
            }

            return result;
        } catch (Exception e) {
            log.error("requestId={}, failed to get presence status of friends, err={}", requestId, e.getMessage(), e);
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_PRESENCE_STATUS, requestId);
        }
    }

    private String buildKey(String userId, String sessionId) {
        return String.format(PRESENCE_KEY, userId, sessionId);
    }

    private String buildPattern(String userId) {
        return String.format(PRESENCE_PATTERN, userId);
    }
}
