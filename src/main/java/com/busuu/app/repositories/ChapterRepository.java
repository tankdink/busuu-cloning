package com.busuu.app.repositories;

import com.busuu.app.dtos.responses.ChapterResponse;
import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {
    boolean existsByChapterOrderAndLevelId (Integer chapterOrder, String levelId);

    List<Chapter> findByCourseIdAndLevelId(String courseId, String levelId);
  
    boolean existsByDescription(String description);

    boolean existsByCourseIdAndLevelId(String courseId, String levelId);

    @Query(value = "SELECT MAX(chapter_order) FROM chapter WHERE level_id = :levelId", nativeQuery = true)
    Integer findMaxChapterOrderByLevelId(@Param("levelId") String levelId);
}