package com.busuu.app.repositories.course;

import com.busuu.app.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {
    boolean existsByTitle (String title);
    boolean existsByCourseOrder (Integer courseOrder);

    @Query(value = "SELECT MAX(course_order) FROM course", nativeQuery = true)
    Integer findMaxCourseOrder();
}
