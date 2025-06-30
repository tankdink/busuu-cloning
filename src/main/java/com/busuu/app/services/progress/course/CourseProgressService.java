package com.busuu.app.services.progress.course;

import com.busuu.app.entities.Course;
import com.busuu.app.entities.CourseLevel;
import com.busuu.app.entities.User;
import com.busuu.app.entities.progresses.CourseProgress;
import com.busuu.app.entities.progresses.LevelProgress;
import com.busuu.app.repositories.progress.CourseProgressRepository;
import com.busuu.app.repositories.progress.LevelProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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
        long completedLevels = courseLevels.stream()
                .map(clv -> levelProgressRepository.findByLevelIdAndUserId(clv.getLevel().getId(), user.getId()))
                .filter(Objects::nonNull)
                .filter(LevelProgress::getIsCompleted)
                .count();

        double progress = (double) completedLevels / courseLevels.size() * 100;

        CourseProgress courseProgress = courseProgressRepository.findByCourseIdAndUserId(course.getId(), user.getId());
        if (courseProgress == null) {
            courseProgress = CourseProgress.builder()
                    .id(UUID.randomUUID().toString())
                    .course(course)
                    .user(user)
                    .progress(progress)
                    .isCompleted(progress >= 80)
                    .build();
        } else {
            courseProgress.setProgress(progress);
            courseProgress.setIsCompleted(progress >= 80);
        }

        return courseProgressRepository.save(courseProgress);
    }
}
