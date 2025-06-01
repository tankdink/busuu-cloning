package com.busuu.app.repositories.progress;

import com.busuu.app.entities.progresses.ChapterProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterProgressRepository extends JpaRepository<ChapterProgress, String> {

    ChapterProgress findChapterProgressByIdAndUserId(String progressId, String userId);
}
