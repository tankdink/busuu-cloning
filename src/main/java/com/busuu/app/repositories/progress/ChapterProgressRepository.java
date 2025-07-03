package com.busuu.app.repositories.progress;

import com.busuu.app.entities.progresses.ChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterProgressRepository extends JpaRepository<ChapterProgress, String> {

    ChapterProgress findByChapterIdAndUserId(String progressId, String userId);

    List<ChapterProgress> findByUserIdAndChapterIdIn(String userId, List<String> chapterIds);
}
