package com.busuu.app.repositories;

import com.busuu.app.entities.User;
import com.busuu.app.entities.enums.FriendshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    boolean existsByEmail (String email);

    Optional<User> findByEmail (String email);

    //Temporary custom query repository, delete later
    @Query(value = """
        SELECT u.* 
        FROM user u 
        JOIN user_role ur ON u.user_id = ur.user_id 
        JOIN role r ON ur.role_id = r.role_id 
        WHERE r.name = :roleName
        """, countQuery = """
        SELECT count(u.user_id) 
        FROM user u 
        JOIN user_role ur ON u.user_id = ur.user_id 
        JOIN role r ON ur.role_id = r.role_id 
        WHERE r.name = :roleName
        """, nativeQuery = true)
    Page<User> findUsersByRoleName(@Param("roleName") String roleName, Pageable pageable);

    Optional<User> findByFacebookAccountId(String facebookAccountId);
    Optional<User> findByGoogleAccountId(String googleAccountId);


    @Modifying
    @Transactional
    @Query("update User u set u.lastSeenAt = :time where u.id = :userId")
    void updateLastSeenAt(String userId, Instant time);

    @Query("SELECT DISTINCT CASE WHEN fs.fromUser.id = :userId THEN fs.toUser.id ELSE fs.fromUser.id END " +
            "FROM Friendship fs " +
            "WHERE (fs.fromUser.id = :userId OR fs.toUser.id = :userId) " +
            "AND fs.status = :friendshipStatus")
    List<String> findFriendIds(@Param("userId") String userId,
                               @Param("friendshipStatus") FriendshipStatus friendShipStatus);

}
