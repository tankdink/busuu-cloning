package com.busuu.app.services.progress.chapter;

import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.ChapterProgress;

public interface IChapterProgressService {

    ChapterProgress upsertChapterProgress (Chapter chapter, User user);
}
