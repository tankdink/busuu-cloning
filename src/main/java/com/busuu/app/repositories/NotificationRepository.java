package com.busuu.app.repositories;

import com.busuu.app.entities.notifications.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String>
{
    Page<Notification> findByUserId(String userId, Pageable pageable);

}
