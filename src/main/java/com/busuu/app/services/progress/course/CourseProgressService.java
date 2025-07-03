package com.busuu.app.services.progress.course;

import com.busuu.app.configs.constant.Constants;
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
        if (courseLevels.isEmpty()) return null;

        List<LevelProgress> progresses = courseLevels.stream()
                .map(clv -> levelProgressRepository.findByLevelIdAndUserId(clv.getLevel().getId(), user.getId()))
                .filter(Objects::nonNull)
                .toList();

        double avgProgress = progresses.isEmpty() ? 0 : progresses.stream().mapToDouble(LevelProgress::getProgress).average().orElse(0);
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
