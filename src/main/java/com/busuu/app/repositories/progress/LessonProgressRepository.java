package com.busuu.app.repositories.progress;

import com.busuu.app.entities.progresses.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, String> {

    LessonProgress findLessonProgressByIdAndUserId(String progressId, String userId);
}
