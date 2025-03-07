package com.busuu.app.repositories;

import com.busuu.app.entities.CourseLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseLevelRepository extends JpaRepository<CourseLevel, String> {
    List<CourseLevel> findByCourseId (String courseId);

    void deleteByCourseId (String courseId);

    boolean existsByCourseIdAndLevelId (String courseId, String levelId);
}
