package com.busuu.app.services.notification;

import com.busuu.app.dtos.responses.NotificationResponse;
import com.busuu.app.entities.enums.NotificationType;
import com.busuu.app.entities.enums.ReactionType;
import org.springframework.data.domain.Page;

public interface INotificationService
{

    void addNotification(String destinationId, NotificationType notificationType, ReactionType reactionType);

    Page<NotificationResponse> getSelfNotification(String requestId, int page, int size);

    void deleteNotification(String requestId, String notificationId);

    NotificationResponse changeStatus(String requestId, String notificationId);

    void changeAllStatus(String requestId);

    Long countUnreadNotifications();

}
