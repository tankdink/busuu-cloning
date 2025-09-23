package com.busuu.app.entities.notifications;

import com.busuu.app.entities.BaseEntity;
import com.busuu.app.entities.User;
import com.busuu.app.entities.posts.Post;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification extends BaseEntity
{
    @Id
    @Column(name = "notification_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "destination_id")
    private String destinationId;

    @Column(name = "actor_id")
    private String actorId;

    @Column(name = "message")
    private String message;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status")
    private NotificationStatus status = NotificationStatus.UNREAD;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType type;

}
