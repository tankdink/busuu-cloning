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

    List<Friendship> findByToUserAndAndStatus(User toUser, FriendshipStatus status);

    Friendship findByFromUserAndToUser(User fromUser, User toUser);

    @Query("""
            SELECT DISTINCT 
            CASE WHEN fs.fromUser.id = :userId THEN fs.toUser.id ELSE fs.fromUser.id END 
            FROM Friendship fs 
            JOIN fs.fromUser fu 
            JOIN fs.toUser tu 
            LEFT JOIN fu.userLanguages fuul 
            LEFT JOIN tu.userLanguages tuul 
            WHERE (fs.fromUser.id = :userId OR fs.toUser.id = :userId) 
                AND fs.status = :friendshipStatus 
                AND (
                       :searchValue IS NULL
                       OR (
                            fs.fromUser.id = :userId 
                            AND LOWER(tu.fullName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
                          )
                       OR (
                            fs.toUser.id = :userId 
                            AND LOWER(fu.fullName) LIKE LOWER(CONCAT('%', :searchValue, '%'))
                          )
                       )
                AND (
                    :languageId IS NULL 
                        OR (fuul.language.id = :languageId AND fuul.speakingStatus <> NO_PROFICIENCY) 
                        OR (tuul.language.id = :languageId AND tuul.speakingStatus <> NO_PROFICIENCY) 
                    )
            """)
    List<String> findFriendIdsWithFilter(@Param("userId") String userId,
                                         @Param("friendshipStatus") FriendshipStatus friendshipStatus,
                                         @Param("searchValue") String searchValue,
                                         @Param("languageId") String languageId);



}
