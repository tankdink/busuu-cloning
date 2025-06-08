package com.busuu.app.services.level;

import com.busuu.app.dtos.requests.level.LevelDTO;
import com.busuu.app.dtos.responses.LevelResponse;
import com.busuu.app.entities.Level;

import java.util.List;

public interface ILevelService
{
    LevelResponse insertLevel(String requestId, LevelDTO levelDTO);
    List<LevelResponse> getLevels(String requestId);
    LevelResponse getLevel(String requestId, String levelID);
    List<LevelResponse> getLevelsByCourseId(String requestId, String courseId);
    LevelResponse updateLevel(String requestId, String levelID, LevelDTO infoUpdateLevel);
    void deleteLevel(String requestId, String levelID);
    LevelResponse getLevelByCode (String requestId, String Code);

}