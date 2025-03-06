package com.busuu.app.repositories;

import com.busuu.app.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String>
{

}
