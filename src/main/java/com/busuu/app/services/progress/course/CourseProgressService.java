package com.busuu.app.services.progress.course;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.entities.Course;
import com.busuu.app.entities.CourseLevel;
import com.busuu.app.entities.Level;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.CourseProgress;
import com.busuu.app.entities.progresses.LevelProgress;
import com.busuu.app.repositories.progress.CourseProgressRepository;
import com.busuu.app.repositories.progress.LevelProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseProgressService implements ICourseProgressService {

    private final LevelProgressRepository levelProgressRepository;
    private final CourseProgressRepository courseProgressRepository;

    @Override
    @Transactional
    public CourseProgress upsertCourseProgress(Course course, User user) {
        List<CourseLevel> courseLevels = course.getCourseLevels();
        if (courseLevels.isEmpty()) return null;

        double totalProgress = 0.0;

        for (CourseLevel cl : courseLevels) {
            Level level = cl.getLevel();
            LevelProgress levelProgress = levelProgressRepository.findByLevelIdAndUserId(level.getId(), user.getId());
            totalProgress += levelProgress != null ? levelProgress.getProgress() : 0;
        }

        double avgProgress = totalProgress / courseLevels.size();
        boolean isCompleted = avgProgress >= Constants.PASSING_PROGRESS;

        CourseProgress courseProgress = courseProgressRepository.findByCourseIdAndUserId(course.getId(), user.getId());
        if (courseProgress == null) {
            courseProgress = CourseProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .course(course)
                    .user(user)
                    .progress(avgProgress)
                    .isCompleted(isCompleted)
                    .build();
        } else {
            courseProgress.setProgress(avgProgress);
            courseProgress.setIsCompleted(isCompleted);
        }

        return courseProgressRepository.save(courseProgress);
    }

}
