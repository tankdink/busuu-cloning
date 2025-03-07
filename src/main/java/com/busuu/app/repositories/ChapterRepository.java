package com.busuu.app.repositories;

import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {
    boolean existsByChapterOrderAndLevelId (Integer chapterOrder, String levelId);
}
