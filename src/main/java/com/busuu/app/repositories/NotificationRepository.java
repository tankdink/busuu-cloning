package com.busuu.app.repositories;

import com.busuu.app.entities.Notification;
import com.busuu.app.entities.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String>
{

    Page<Notification> findByUserId(String userId, Pageable pageable);

    long countByUserIdAndStatus(String userId, NotificationStatus status);

    List<Notification> findByUserIdAndStatus(String userId, NotificationStatus status);

}
