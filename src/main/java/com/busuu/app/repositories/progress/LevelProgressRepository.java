package com.busuu.app.repositories.progress;

import com.busuu.app.entities.progresses.LevelProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LevelProgressRepository extends JpaRepository<LevelProgress, String> {
    LevelProgress findByLevelIdAndUserId(String progressId, String userId);
}
