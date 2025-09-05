package com.busuu.app.repositories;

import com.busuu.app.entities.UserWordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWordHistoryRepository extends JpaRepository<UserWordHistory, String> {

}
