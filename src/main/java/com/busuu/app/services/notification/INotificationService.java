package com.busuu.app.services.notification;

import com.busuu.app.dtos.responses.NotificationResponse;
import com.busuu.app.entities.User;
import com.busuu.app.entities.notifications.Notification;
import com.busuu.app.entities.notifications.NotificationStatus;
import com.busuu.app.entities.notifications.NotificationType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface INotificationService
{

    void addNotification(String destinationId, NotificationType notificationType);

    Page<NotificationResponse> getSelfNotification(String requestId, int page, int size);

    void deleteNotification(String requestId, String notificationId);

    NotificationResponse changeStatus(String requestId, String notificationId);

}
