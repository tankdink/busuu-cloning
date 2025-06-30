package com.busuu.app.services.progress.level;

import com.busuu.app.entities.Level;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.LevelProgress;

public interface ILevelProgressService {

    LevelProgress upsertLevelProgress (Level level, User user);
}
