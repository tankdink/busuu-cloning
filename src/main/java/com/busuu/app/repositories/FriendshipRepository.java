package com.busuu.app.repositories;

import com.busuu.app.entities.Friendship;
import com.busuu.app.entities.User;
import com.busuu.app.entities.enums.FriendshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, String>, JpaSpecificationExecutor<Friendship>
{

    boolean existsByFromUserAndToUserAndStatus(User fromUser, User toUser, FriendshipStatus status);

    Friendship findByFromUserAndToUserAndStatus(User fromUser, User toUser, FriendshipStatus status);

    Friendship findByFromUserAndToUser(User fromUser, User toUser);

    @Query("SELECT CASE WHEN fs.fromUser.id = :userId THEN fs.toUser.id ELSE fs.fromUser.id END " +
            "FROM Friendship fs " +
            "JOIN User fu ON fs.fromUser.id = fu.id " +
            "JOIN User tu ON fs.toUser.id = tu.id " +
            "WHERE (fs.fromUser.id = :userId OR fs.toUser.id = :userId) " +
            "AND fs.status = :friendshipStatus " +
            "AND (:searchValue IS NULL OR fu.fullName LIKE %:searchValue% OR tu.fullName LIKE %:searchValue%) " +
            "AND (:country IS NULL OR fu.country = :country OR tu.country = :country) " +
            "ORDER BY fu.fullName, tu.fullName")
    List<String> findFriendIdsWithFilter(@Param("userId") String userId,
                                         @Param("friendshipStatus") FriendshipStatus friendshipStatus,
                                         @Param("searchValue") String searchValue,
                                         @Param("country") String country);


}
