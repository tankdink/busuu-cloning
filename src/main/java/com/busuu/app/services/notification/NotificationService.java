package com.busuu.app.services.notification;


import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.FriendshipResponse;
import com.busuu.app.dtos.responses.NotificationResponse;
import com.busuu.app.entities.Language;
import com.busuu.app.entities.User;
import com.busuu.app.entities.corrections.Correction;
import com.busuu.app.entities.enums.FriendshipStatus;
import com.busuu.app.entities.enums.PresenceStatus;
import com.busuu.app.entities.notifications.Notification;
import com.busuu.app.entities.notifications.NotificationStatus;
import com.busuu.app.entities.notifications.NotificationType;
import com.busuu.app.entities.posts.Post;
import com.busuu.app.entities.reactions.ReactionType;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.repositories.CorrectionRepository;
import com.busuu.app.repositories.NotificationRepository;
import com.busuu.app.repositories.PostRepository;
import com.busuu.app.repositories.UserRepository;
import com.busuu.app.services.publisher.NotificationEventPublisher;
import com.busuu.app.services.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService implements INotificationService
{

    private final NotificationRepository notificationRepository;

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final CorrectionRepository correctionRepository;

    private final NotificationEventPublisher notificationEventPublisher;


    @Override
    @Transactional
    public void addNotification(String destinationId, NotificationType notificationType, ReactionType reactionType)
    {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User userActor = (User) auth.getPrincipal();
            String actorId = userActor.getId();

            boolean giveNotification = true;
            String message = "";
            User toUser = null;

            switch (notificationType)
            {
                //DestinationId: User
                case FRIEND_REQUESTED:
                {
                    User destinationUser = userRepository.findById(destinationId)
                            .orElseThrow( ()-> new DataNotFoundException("Cannot find user with ID"));

                    message = " has sent you a friend request";
                    toUser = destinationUser;

                    break;
                }
                //DestinationId: User
                case FRIEND_ACCEPTED:
                {
                    User destinationUser = userRepository.findById(destinationId)
                            .orElseThrow( ()-> new DataNotFoundException("Cannot find user with ID"));

                    message = " has accepted your friend request";
                    toUser = destinationUser;

                    break;
                }
                //DestinationId: Post
                case POST_CORRECTED:
                {
                    Post destinationPost = postRepository.findById(destinationId)
                            .orElseThrow( ()-> new DataNotFoundException("Cannot find post with ID"));

                    message = " has corrected your post";
                    toUser = destinationPost.getUser();

                    break;
                }
                //DestinationId: Post
                case CORRECTION_REACTION:
                {
                    Correction destinationCorrection = correctionRepository.findById(destinationId)
                            .orElseThrow( ()-> new DataNotFoundException("Cannot find post with ID"));

                    destinationId = destinationCorrection.getPost().getId();

                    message = " has " + reactionType.name().toLowerCase() +  "d your correction";
                    toUser = destinationCorrection.getUser();

                    break;
                }
                //DestinationId: Post
                case CORRECTION_REPLIED:
                {
                    Correction destinationCorrection = correctionRepository.findById(destinationId)
                             .orElseThrow( ()-> new DataNotFoundException("Cannot find correction with ID"));

                    destinationId = destinationCorrection.getPost().getId();

                    message = " has replied your correction";

                    if (destinationCorrection.getUser().getId().equals(userActor.getId())) giveNotification = false;
                    toUser = destinationCorrection.getUser();

                    break;
                }

                default: break;
            }

            if (giveNotification)
            {
                UUID uuid = UUID.randomUUID();
                Notification newNotification = Notification.builder()
                        .id(uuid.toString())
                        .user(toUser)
                        .destinationId(destinationId)
                        .actorId(actorId)
                        .message(message)
                        .type(notificationType)
                        .build();

                notificationRepository.save(newNotification);

                //Socket
                notificationEventPublisher.publishNotification(uuid.toString(), destinationId, actorId, message, notificationType, toUser.getId());

            }


        } catch (Exception e) {
            log.error("Failed to add notification, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_NOTIFICATION, "Internal request");
        }

    }

    @Override
    public Page<NotificationResponse> getSelfNotification(String requestId, int page, int size)
    {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            Sort sort = Sort.by(
                    Sort.Order.desc("status"),
                    Sort.Order.desc("createdAt")
            );

            return notificationRepository.findByUserId(userId, PageRequest.of(page, size, sort)).map(
                    notification -> modelMapper.map(notification, NotificationResponse.class)

            );

        } catch (Exception e) {
        log.error("Failed to add notification, err="+e.getMessage());
        throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                Constants.ERROR_CODE.ERR_GET_NOTIFICATION, requestId);
        }

    }

    @Override
    public void deleteNotification(String requestId, String notificationId)
    {
        try {

            Notification existsNotification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find notification with ID = " + notificationId));

            notificationRepository.deleteById(notificationId);

        } catch (Exception e) {
            log.error("Failed to add notification, err=" + e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_NOTIFICATION, requestId);
        }
    }

    @Override
    public NotificationResponse changeStatus(String requestId, String notificationId)
    {
        try {

            Notification existsNotification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find notification with ID = " + notificationId));

            if (existsNotification.getStatus().equals(NotificationStatus.UNREAD)) existsNotification.setStatus(NotificationStatus.READ);
            else existsNotification.setStatus(NotificationStatus.UNREAD);

            NotificationResponse response = modelMapper.map(notificationRepository.save(existsNotification),  NotificationResponse.class);

            return response;

        } catch (Exception e) {
            log.error("Failed to add notification, err=" + e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CHANGE_STATUS_NOTIFICATION, requestId);
        }
    }

    @Override
    public Long countUnreadNotifications()
    {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) auth.getPrincipal();
        String userId = user.getId();

        return notificationRepository.countByUserIdAndStatus(userId, NotificationStatus.UNREAD);
    }
}
