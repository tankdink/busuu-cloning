package com.busuu.app.repositories.progress;

import com.busuu.app.entities.progresses.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgress, String> {

    LessonProgress findByLessonIdAndUserId(String lessonId, String userId);

    List<LessonProgress> findByUserIdAndLessonIdIn(String userId, List<String> lessonIds);
}
