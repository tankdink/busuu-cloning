package com.busuu.app.repositories.course;

import com.busuu.app.entities.CourseLevel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseLevelRepository extends JpaRepository<CourseLevel, String> {
    List<CourseLevel> findByCourseId (String courseId);

    @Query("SELECT a FROM CourseLevel a JOIN a.level b WHERE a.course.id = :courseId ORDER BY b.code ASC")
    List<CourseLevel> findByCourseIdWithSortingLevel(@Param("courseId") String courseId);

    void deleteByCourseId (String courseId);

    boolean existsByCourseIdAndLevelId (String courseId, String levelId);
}
