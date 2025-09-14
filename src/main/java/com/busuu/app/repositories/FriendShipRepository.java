package com.busuu.app.repositories;

import com.busuu.app.entities.FriendShip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendShipRepository extends JpaRepository<FriendShip, String> {

}
