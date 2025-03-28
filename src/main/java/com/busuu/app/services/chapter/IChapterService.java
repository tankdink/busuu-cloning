package com.busuu.app.services.chapter;

import com.busuu.app.dtos.requests.chapter.ChapterDTO;
import com.busuu.app.dtos.responses.ChapterResponse;


import java.util.List;

public interface IChapterService
{
    ChapterResponse insertChapter(String requestId, ChapterDTO chapterDTO);
    List<ChapterResponse> getChapters(String requestId);
    ChapterResponse getChapter(String requestId, String chapterID);
    List<ChapterResponse> getByCourseIdAndLevelId(String requestId, String courseID, String levelId);
    ChapterResponse updateChapter(String requestId, String chapterID, ChapterDTO infoUpdateChapter);
    void deleteChapter(String requestId, String chapterID);

}
