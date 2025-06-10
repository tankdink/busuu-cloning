package com.busuu.app.repositories;

import com.busuu.app.entities.Lesson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {

    boolean existsByTitleAndChapterId (String title, String chapterId);

    boolean existsByLessonOrderAndChapterId (Integer lessonOrder, String chapterId);

    boolean existsByChapterId (String chapterId);

    @Query(value = "SELECT MAX(lesson_order) FROM lesson WHERE chapter_id = :chapterId", nativeQuery = true)
    Integer findMaxGrammarSectionOrderByChapterId(@Param("chapterId") String chapterId);

    List<Lesson> findByChapterId(String chapterId, Sort sort);

    @Query(value = """
            SELECT l.* 
            FROM lesson l 
            JOIN chapter c ON l.chapter_id = c.chapter_id 
            JOIN level lv ON c.level_id = lv.level_id 
            ORDER BY c.course_id, lv.code, c.chapter_order, lesson_order
            """,
            countQuery = """
            SELECT count(l.lesson_id) 
            FROM lesson l
            """, nativeQuery = true)
    Page<Lesson> findAll(Pageable pageable);
}
