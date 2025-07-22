package com.busuu.app.repositories;

import com.busuu.app.dtos.responses.ChapterResponse;
import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String>, JpaSpecificationExecutor<Chapter> {
    boolean existsByChapterOrderAndLevelId (Integer chapterOrder, String levelId);

    List<Chapter> findByCourseIdAndLevelId(String courseId, String levelId, Sort sort);
  
    boolean existsByTitle(String title);

    boolean existsByCourseIdAndLevelId(String courseId, String levelId);

    @Query(value = "SELECT MAX(chapter_order) FROM chapter WHERE level_id = :levelId AND course_id = :courseId", nativeQuery = true)
    Integer findMaxChapterOrderByLevelIdAndCourseId(@Param("levelId") String levelId, @Param("courseId") String courseId);


    //Temporary custom query repository, delete later
    @Query(value = "SELECT c.* " +
                    "FROM chapter c " +
                    "JOIN level lv ON c.level_id = lv.level_id " +
                    "ORDER BY course_id, CODE, chapter_order, chapter_id",
            countQuery = """
                    SELECT count(c.chapter_id) 
                    FROM chapter c 
                    """, nativeQuery = true)
    Page<Chapter> findAll(Pageable pageable);


}