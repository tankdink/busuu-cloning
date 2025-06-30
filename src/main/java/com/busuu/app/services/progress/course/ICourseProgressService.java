package com.busuu.app.services.progress.course;

import com.busuu.app.entities.Course;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.CourseProgress;

public interface ICourseProgressService {

    CourseProgress upsertCourseProgress(Course course, User user);
}
