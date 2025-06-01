package com.busuu.app.repositories;

import com.busuu.app.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {

    boolean existsByTitleAndChapterId (String title, String chapterId);

    boolean existsByLessonOrderAndChapterId (Integer lessonOrder, String chapterId);

    List<Lesson> findByChapterId (String chapterId);

    boolean existsByChapterId (String chapterId);

    @Query(value = "SELECT MAX(lesson_order) FROM lesson WHERE chapter_id = :chapterId", nativeQuery = true)
    Integer findMaxGrammarSectionOrderByChapterId(@Param("chapterId") String chapterId);
}
