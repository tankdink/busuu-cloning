package com.busuu.app.repositories;

import com.busuu.app.entities.Friend;
import com.busuu.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, String>, JpaSpecificationExecutor<Friend>
{

    boolean existsByUserAndFriend(User user, User friend);

    boolean existsByFriendAndUser(User friend, User user);

    void deleteByUserIdAndFriendId(String userId, String friendId);

    List<Friend> findByUserId(String userId);



}
