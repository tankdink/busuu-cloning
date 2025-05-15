package com.busuu.app.services.level;

import com.busuu.app.dtos.requests.level.LevelDTO;
import com.busuu.app.entities.Level;

import java.util.List;

public interface ILevelService
{
    Level insertLevel(String requestId, LevelDTO levelDTO);
    List<Level> getLevels(String requestId);
    Level getLevel(String requestId, String levelID);
    List<Level> getLevelsByCourseId(String requestId, String courseId);
    Level updateLevel(String requestId, String levelID, LevelDTO infoUpdateLevel);
    void deleteLevel(String requestId, String levelID);
    Level getLevelByCode (String requestId, String Code);

}