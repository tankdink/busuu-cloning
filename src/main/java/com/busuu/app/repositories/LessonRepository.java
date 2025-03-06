package com.busuu.app.repositories;

import com.busuu.app.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {

    boolean existsByTitle (String title);

    boolean existsByLessonOrder (Integer lessonOrder);
}
