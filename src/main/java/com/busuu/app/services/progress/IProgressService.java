package com.busuu.app.services.progress;

import com.busuu.app.dtos.requests.progress.CourseProgressDTO;
import com.busuu.app.dtos.requests.progress.GrammarProgressDTO;
import com.busuu.app.dtos.responses.ProgressResponse;

public interface IProgressService {

    ProgressResponse upSertCourseProgress (String requestId, CourseProgressDTO progressDTO);

    ProgressResponse upSertGrammarProgress (String requestId, GrammarProgressDTO progressDTO);

    ProgressResponse getObjectProgress (String requestId, String objectId, String userId, String objectName);
}
