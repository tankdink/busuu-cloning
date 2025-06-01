package com.busuu.app.services.progress;

import com.busuu.app.dtos.responses.ProgressResponse;

public interface IProgressService {

    ProgressResponse upSertCourseProgress (String requestId, String userId, String courseId, String levelId, String chapterId, String lessonId);

    ProgressResponse upSertGrammarProgress (String requestId, String userId, String grammarSectionId, String grammarId);

    ProgressResponse getObjectProgress (String requestId, String objectId, String userId, String objectName);
}
