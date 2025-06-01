package com.busuu.app.repositories.progress;

import com.busuu.app.entities.progresses.CourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, String> {
    CourseProgress findCourseProgressByIdAndUserId(String progressId, String userId);
}
