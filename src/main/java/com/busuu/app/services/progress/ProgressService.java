package com.busuu.app.services.progress;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.responses.ProgressResponse;
import com.busuu.app.entities.*;
import com.busuu.app.entities.progresses.*;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.ChapterRepository;
import com.busuu.app.repositories.LessonRepository;
import com.busuu.app.repositories.LevelRepository;
import com.busuu.app.repositories.UserRepository;
import com.busuu.app.repositories.course.CourseRepository;
import com.busuu.app.repositories.grammar.GrammarRepository;
import com.busuu.app.repositories.grammar.GrammarSectionRepository;
import com.busuu.app.repositories.progress.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressService implements IProgressService {

    // Repository Progress
    private final CourseProgressRepository courseProgressRepository;
    private final LevelProgressRepository levelProgressRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final GrammarProgressRepository grammarProgressRepository;
    private final GrammarSectionProgressRepository grammarSectionProgressRepository;

    // Repository Entity
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final LevelRepository levelRepository;
    private final CourseRepository courseRepository;
    private final GrammarSectionRepository grammarSectionRepository;
    private final GrammarRepository grammarRepository;

    @Override
    @Transactional
    public ProgressResponse upSertCourseProgress(String requestId, String userId, String courseId, String levelId, String chapterId, String lessonId) {
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + userId));

            // Check constraints
            boolean constraintCourseLevel = chapterRepository.existsByCourseIdAndLevelId(courseId, levelId);
            if (constraintCourseLevel) {
                boolean constraintLessonChapter = lessonRepository.existsByChapterId(chapterId);
                if (!constraintLessonChapter) {
                    throw new DataNotFoundException("Cannot find Lesson ID = " + lessonId + "of Chapter ID = " + chapterId);
                }
            }
            else {
                throw new DataNotFoundException("Cannot find Chapter ID = " + chapterId + " of Level ID = " + levelId + " of Course ID = " + courseId);
            }

            Lesson existingLesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + lessonId));

            if (existingLesson.getQuestions().isEmpty()) {
                throw new DataNotFoundException("Cannot find list Question with Lesson ID = " + lessonId);
            }

            LessonProgress lessonProgress = lessonProgressRepository.findLessonProgressByIdAndUserId(lessonId, userId);
            if (lessonProgress == null) {
                lessonProgress = LessonProgress.builder()
                        .id(UUID.randomUUID().toString())
                        .isCompleted(false)
                        .user(existingUser)
                        .lesson(existingLesson)
                        .progress((double) (100 / existingLesson.getQuestions().size()))
                        .build();
            } else {
                lessonProgress.setProgress(lessonProgress.getProgress() + 100 / existingLesson.getQuestions().size());
                if (lessonProgress.getProgress() == 100) {
                    lessonProgress.setIsCompleted(true);
                }
            }
            lessonProgress = lessonProgressRepository.save(lessonProgress);

            Chapter existingChapter = chapterRepository.findById(chapterId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Chapter with ID = " + chapterId));

            ChapterProgress chapterProgress = chapterProgressRepository.findChapterProgressByIdAndUserId(chapterId, userId);
            if (chapterProgress == null) {
                chapterProgress = ChapterProgress.builder()
                        .id(UUID.randomUUID().toString())
                        .isCompleted(false)
                        .chapter(existingChapter)
                        .user(existingUser)
                        .progress(0.0)
                        .build();
            } else {
                if (lessonProgress.getIsCompleted()) {
                    chapterProgress.setProgress(chapterProgress.getProgress() + 100 / existingChapter.getLessons().size());
                    if (chapterProgress.getProgress() == 100) {
                        chapterProgress.setIsCompleted(true);
                    }
                }
            }
            chapterProgress = chapterProgressRepository.save(chapterProgress);

            Level existingLevel = levelRepository.findById(levelId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Level with ID = " + levelId));

            LevelProgress levelProgress = levelProgressRepository.findLevelProgressByIdAndUserId(levelId, userId);
            if (levelProgress == null) {
                levelProgress = LevelProgress.builder()
                        .id(UUID.randomUUID().toString())
                        .isCompleted(false)
                        .level(existingLevel)
                        .user(existingUser)
                        .progress(0.0)
                        .build();
            } else {
                if (chapterProgress.getIsCompleted()) {
                    levelProgress.setProgress(levelProgress.getProgress() + 100 / existingLevel.getChapters().size());
                    if (levelProgress.getProgress() == 100) {
                        levelProgress.setIsCompleted(true);
                    }
                }
            }
            levelProgress = levelProgressRepository.save(levelProgress);

            Course existingCourse = courseRepository.findById(courseId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Course with ID = " + courseId));

            CourseProgress courseProgress = courseProgressRepository.findCourseProgressByIdAndUserId(courseId, userId);
            if (courseProgress == null) {
                courseProgress = CourseProgress.builder()
                        .id(UUID.randomUUID().toString())
                        .isCompleted(false)
                        .course(existingCourse)
                        .user(existingUser)
                        .progress(0.0)
                        .build();
            } else {
                if (chapterProgress.getIsCompleted()) {
                    courseProgress.setProgress(courseProgress.getProgress() + 100 / existingCourse.getCourseLevels().size());
                    if (courseProgress.getProgress() == 100) {
                        courseProgress.setIsCompleted(true);
                    }
                }
            }
            courseProgress = courseProgressRepository.save(courseProgress);

            return ProgressResponse.builder()
                    .userId(userId)
                    .objectName("Course Progress")
                    .progress(courseProgress.getProgress())
                    .objectId(courseId)
                    .isCompleted(courseProgress.getIsCompleted())
                    .id(courseProgress.getId())
                    .build();
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to upsert object progress, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPSERT_PROGRESS, requestId);
        }
    }

    @Override
    @Transactional
    public ProgressResponse upSertGrammarProgress(String requestId, String userId, String grammarSectionId, String grammarId) {
        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + userId));

            // Check constraints
            boolean constraints = grammarSectionRepository.existsGrammarSectionByGrammarId(grammarId);
            if (!constraints) {
                throw new DataNotFoundException("Cannot find Grammar Section with ID = " + grammarSectionId + " of Grammar ID = " + grammarId);
            }

            GrammarSection existingGrammarSection = grammarSectionRepository.findById(grammarSectionId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot found Grammar Section with ID = " + grammarSectionId));

            GrammarSectionProgress grammarSectionProgress = grammarSectionProgressRepository.findGrammarSectionProgressByIdAndUserId(grammarSectionId, userId);
            if (grammarSectionProgress == null) {
                grammarSectionProgress = GrammarSectionProgress.builder()
                        .id(UUID.randomUUID().toString())
                        .isCompleted(false)
                        .progress((double) (100 / existingGrammarSection.getQuestions().size()))
                        .grammarSection(existingGrammarSection)
                        .user(existingUser)
                        .build();
            } else {
                grammarSectionProgress.setProgress(grammarSectionProgress.getProgress() + 100 / existingGrammarSection.getQuestions().size());
                if (grammarSectionProgress.getProgress() == 100) {
                    grammarSectionProgress.setIsCompleted(true);
                }
            }
            grammarSectionProgress = grammarSectionProgressRepository.save(grammarSectionProgress);

            Grammar existingGrammar = grammarRepository.findById(grammarId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Grammar with ID = " + grammarId));

            GrammarProgress grammarProgress = grammarProgressRepository.findGrammarProgressByIdAndUserId(grammarId, userId);
            if (grammarProgress == null) {
                grammarProgress = GrammarProgress.builder()
                        .id(UUID.randomUUID().toString())
                        .isCompleted(false)
                        .grammar(existingGrammar)
                        .user(existingUser)
                        .progress((double) (100 / existingGrammar.getGrammarSections().size()))
                        .build();
            } else {
                grammarProgress.setProgress(grammarProgress.getProgress() + 100 / existingGrammar.getGrammarSections().size());
                if (grammarProgress.getProgress() == 100) {
                    grammarProgress.setIsCompleted(true);
                }
            }
            grammarProgress = grammarProgressRepository.save(grammarProgress);

            return ProgressResponse.builder()
                    .isCompleted(grammarProgress.getIsCompleted())
                    .objectId(grammarId)
                    .progress(grammarProgress.getProgress())
                    .objectName("Grammar Progress")
                    .userId(userId)
                    .id(grammarProgress.getId())
                    .build();

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to upsert object progress, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPSERT_PROGRESS, requestId);
        }
    }

    @Override
    public ProgressResponse getObjectProgress(String requestId, String objectId, String userId, String objectName) {
        try {
            switch (objectName) {
                case "COURSE" -> {
                    CourseProgress courseProgress = courseProgressRepository.findCourseProgressByIdAndUserId(objectId, userId);
                    return ProgressResponse.builder()
                            .id(courseProgress.getId())
                            .isCompleted(courseProgress.getIsCompleted())
                            .objectId(objectId)
                            .objectName("Course Progress")
                            .progress(courseProgress.getProgress())
                            .userId(userId)
                            .build();
                }
                case "LEVEL" -> {
                    LevelProgress levelProgress = levelProgressRepository.findLevelProgressByIdAndUserId(objectId, userId);
                    return ProgressResponse.builder()
                            .id(levelProgress.getId())
                            .isCompleted(levelProgress.getIsCompleted())
                            .objectId(objectId)
                            .objectName("Level Progress")
                            .progress(levelProgress.getProgress())
                            .userId(userId)
                            .build();
                }
                case "CHAPTER" -> {
                    ChapterProgress chapterProgress = chapterProgressRepository.findChapterProgressByIdAndUserId(objectId, userId);
                    return ProgressResponse.builder()
                            .id(chapterProgress.getId())
                            .isCompleted(chapterProgress.getIsCompleted())
                            .objectId(objectId)
                            .objectName("Chapter Progress")
                            .progress(chapterProgress.getProgress())
                            .userId(userId)
                            .build();
                }
                case "LESSON" -> {
                    LessonProgress lessonProgress = lessonProgressRepository.findLessonProgressByIdAndUserId(objectId, userId);
                    return ProgressResponse.builder()
                            .id(lessonProgress.getId())
                            .isCompleted(lessonProgress.getIsCompleted())
                            .objectId(objectId)
                            .objectName("Lesson Progress")
                            .progress(lessonProgress.getProgress())
                            .userId(userId)
                            .build();
                }
                case "GRAMMAR" -> {
                    GrammarProgress grammarProgress = grammarProgressRepository.findGrammarProgressByIdAndUserId(objectId, userId);
                    return ProgressResponse.builder()
                            .id(grammarProgress.getId())
                            .isCompleted(grammarProgress.getIsCompleted())
                            .objectId(objectId)
                            .objectName("Grammar Progress")
                            .progress(grammarProgress.getProgress())
                            .userId(userId)
                            .build();
                }
                case "GRAMMAR_SECTION" -> {
                    GrammarSectionProgress grammarSectionProgress = grammarSectionProgressRepository.findGrammarSectionProgressByIdAndUserId(objectId, userId);
                    return ProgressResponse.builder()
                            .id(grammarSectionProgress.getId())
                            .isCompleted(grammarSectionProgress.getIsCompleted())
                            .objectId(objectId)
                            .objectName("Grammar Section Progress")
                            .progress(grammarSectionProgress.getProgress())
                            .userId(userId)
                            .build();
                }
                default -> throw new ExistDataException("Object name is not valid");
            }
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get object progress, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_PROGRESS, requestId);
        }
    }
}
