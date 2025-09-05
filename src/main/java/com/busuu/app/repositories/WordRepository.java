package com.busuu.app.repositories;

import com.busuu.app.entities.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<Word, String> {

    @Query("""
    SELECT w FROM Word w
    WHERE (:lessonId IS NULL OR w.lesson.id = :lessonId)
      AND (
           :keyword IS NULL
           OR LOWER(w.text) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(w.translation) LIKE LOWER(CONCAT('%', :keyword, '%'))
      )
""")
    Page<Word> findAllWithFilter(
            @Param("lessonId") String lessonId,
            @Param("keyword") String keyword,
            Pageable pageable
    );


    Page<Word> findByLessonId(String lessonId, Pageable pageable);
}
