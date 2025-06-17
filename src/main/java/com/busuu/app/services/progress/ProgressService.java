package com.busuu.app.services.progress;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.progress.CourseProgressDTO;
import com.busuu.app.dtos.requests.progress.GrammarProgressDTO;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ProgressResponse upSertCourseProgress(String requestId, CourseProgressDTO progressDTO) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + userId));

            // Check constraints
            boolean constraintCourseLevel = chapterRepository.existsByCourseIdAndLevelId(progressDTO.getCourseId(), progressDTO.getLevelId());
            if (constraintCourseLevel) {
                boolean constraintLessonChapter = lessonRepository.existsByChapterId(progressDTO.getChapterId());
                if (!constraintLessonChapter) {
                    throw new DataNotFoundException("Cannot find Lesson ID = " + progressDTO.getLessonId() + "of Chapter ID = " + progressDTO.getChapterId());
                }
            }
            else {
                throw new DataNotFoundException("Cannot find Chapter ID = " + progressDTO.getChapterId() + " of Level ID = " + progressDTO.getLevelId() + " of Course ID = " + progressDTO.getCourseId());
            }

            Lesson existingLesson = lessonRepository.findById(progressDTO.getLessonId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + progressDTO.getLessonId()));

            if (existingLesson.getQuestions().isEmpty()) {
                throw new DataNotFoundException("Cannot find list Question with Lesson ID = " + progressDTO.getLessonId());
            }

            LessonProgress lessonProgress = lessonProgressRepository.findByLessonIdAndUserId(progressDTO.getLessonId(), userId);
            double progressL = ((double) progressDTO.getNumberQuestions() / existingLesson.getQuestions().size()) * 100;
            if (lessonProgress == null) {
                lessonProgress = LessonProgress.builder()
                        .id(UUID.randomUUID().toString())
                        .isCompleted(progressL >= 80)
                        .user(existingUser)
                        .lesson(existingLesson)
                        .progress(progressL)
                        .build();
            } else {
                lessonProgress.setProgress(progressL >= lessonProgress.getProgress() ? progressL : lessonProgress.getProgress());
                lessonProgress.setIsCompleted(lessonProgress.getProgress() >= 80);
            }
            LessonProgress newLessonProgress = lessonProgressRepository.save(lessonProgress);

            Chapter existingChapter = chapterRepository.findById(progressDTO.getChapterId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Chapter with ID = " + progressDTO.getChapterId()));

            ChapterProgress chapterProgress = chapterProgressRepository.findByChapterIdAndUserId(progressDTO.getChapterId(), userId);
            if (chapterProgress == null) {
                double progressC = newLessonProgress.getIsCompleted() ? ((double) 1 / existingChapter.getLessons().size()) * 100 : 0.0;
                chapterProgress = ChapterProgress.builder()
                        .id(UUID.randomUUID().toString())
                        .isCompleted(progressC >= 80)
                        .chapter(existingChapter)
                        .user(existingUser)
                        .progress(progressC)
                        .build();
            } else {
                if (!lessonProgress.getIsCompleted() && newLessonProgress.getIsCompleted()) {
                    chapterProgress.setProgress(chapterProgress.getProgress() + 100 / existingChapter.getLessons().size());
                    chapterProgress.setIsCompleted(chapterProgress.getProgress() >= 80);
                }
            }
            ChapterProgress newChapterProgress = chapterProgressRepository.save(chapterProgress);

            Level existingLevel = levelRepository.findById(progressDTO.getLevelId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Level with ID = " + progressDTO.getLevelId()));

            LevelProgress levelProgress = levelProgressRepository.findByLevelIdAndUserId(progressDTO.getLevelId(), userId);
            if (levelProgress == null) {
                double progressLe = newChapterProgress.getIsCompleted() ? ((double) 1 / existingLevel.getChapters().size()) * 100 : 0.0;
                levelProgress = LevelProgress.builder()
                        .id(UUID.randomUUID().toString())
                        .isCompleted(progressLe >= 80)
                        .level(existingLevel)
                        .user(existingUser)
                        .progress(progressLe)
                        .build();
            } else {
                if (!chapterProgress.getIsCompleted() && newChapterProgress.getIsCompleted()) {
                    levelProgress.setProgress(levelProgress.getProgress() + 100 / existingLevel.getChapters().size());
                    levelProgress.setIsCompleted(levelProgress.getProgress() >= 80);
                }
            }
            LevelProgress newLevelProgress = levelProgressRepository.save(levelProgress);

            Course existingCourse = courseRepository.findById(progressDTO.getCourseId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Course with ID = " + progressDTO.getCourseId()));

            CourseProgress courseProgress = courseProgressRepository.findByCourseIdAndUserId(progressDTO.getCourseId(), userId);
            if (courseProgress == null) {
                double progressCo = newLevelProgress.getIsCompleted() ? ((double) 1 / existingCourse.getCourseLevels().size()) * 100 : 0.0;
                courseProgress = CourseProgress.builder()
                        .id(UUID.randomUUID().toString())
                        .isCompleted(progressCo >= 80)
                        .course(existingCourse)
                        .user(existingUser)
                        .progress(progressCo)
                        .build();
            } else {
                if (!levelProgress.getIsCompleted() && newLevelProgress.getIsCompleted()) {
                    courseProgress.setProgress(courseProgress.getProgress() + 100 / existingCourse.getCourseLevels().size());
                    courseProgress.setIsCompleted(courseProgress.getProgress() >= 80);
                }
            }
            courseProgress = courseProgressRepository.save(courseProgress);

            return ProgressResponse.builder()
                    .userId(userId)
                    .objectName("Lesson Progress")
                    .progress(lessonProgress.getProgress())
                    .objectId(progressDTO.getLessonId())
                    .isCompleted(lessonProgress.getIsCompleted())
                    .id(lessonProgress.getId())
                    .build();
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to upsert object progress, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPSERT_PROGRESS, requestId);
        }
    }

    @Override
    @Transactional
    public ProgressResponse upSertGrammarProgress(String requestId, GrammarProgressDTO progressDTO) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find User with ID = " + userId));

            // Check constraints
            boolean constraints = grammarSectionRepository.existsGrammarSectionByGrammarId(progressDTO.getGrammarId());
            if (!constraints) {
                throw new DataNotFoundException("Cannot find Grammar Section with ID = " + progressDTO.getGrammarSectionId() + " of Grammar ID = " + progressDTO.getGrammarId());
            }

            GrammarSection existingGrammarSection = grammarSectionRepository.findById(progressDTO.getGrammarSectionId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot found Grammar Section with ID = " + progressDTO.getGrammarSectionId()));

            GrammarSectionProgress grammarSectionProgress = grammarSectionProgressRepository.findByGrammarSectionIdAndUserId(progressDTO.getGrammarSectionId(), userId);
            double progressGs = ((double) progressDTO.getNumberQuestions() / existingGrammarSection.getQuestions().size()) * 100;
            if (grammarSectionProgress == null) {
                grammarSectionProgress = GrammarSectionProgress.builder()
                        .id(UUID.randomUUID().toString())
                        .isCompleted(progressGs >= 80)
                        .progress(progressGs)
                        .grammarSection(existingGrammarSection)
                        .user(existingUser)
                        .build();
            } else {
                grammarSectionProgress.setProgress(progressGs >= grammarSectionProgress.getProgress() ? progressGs : grammarSectionProgress.getProgress());
                grammarSectionProgress.setIsCompleted(grammarSectionProgress.getProgress() >= 80);
            }
            GrammarSectionProgress newGrammarSectionProgress = grammarSectionProgressRepository.save(grammarSectionProgress);

            Grammar existingGrammar = grammarRepository.findById(progressDTO.getGrammarId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Grammar with ID = " + progressDTO.getGrammarId()));

            GrammarProgress grammarProgress = grammarProgressRepository.findByGrammarIdAndUserId(progressDTO.getGrammarId(), userId);
            if (grammarProgress == null) {
                double progressG = newGrammarSectionProgress.getIsCompleted() ? ((double) 1 / existingGrammar.getGrammarSections().size()) * 100 : 0.0;
                grammarProgress = GrammarProgress.builder()
                        .id(UUID.randomUUID().toString())
                        .isCompleted(progressG >= 80)
                        .grammar(existingGrammar)
                        .user(existingUser)
                        .progress(progressG)
                        .build();
            } else {
                if (!grammarSectionProgress.getIsCompleted() && newGrammarSectionProgress.getIsCompleted()) {
                    grammarProgress.setProgress(grammarProgress.getProgress() + 100 / existingGrammar.getGrammarSections().size());
                    grammarProgress.setIsCompleted(grammarProgress.getProgress() >= 80);
                }
            }
            grammarProgress = grammarProgressRepository.save(grammarProgress);

            return ProgressResponse.builder()
                    .isCompleted(grammarSectionProgress.getIsCompleted())
                    .objectId(progressDTO.getGrammarSectionId())
                    .progress(grammarSectionProgress.getProgress())
                    .objectName("Grammar Section Progress")
                    .userId(userId)
                    .id(grammarSectionProgress.getId())
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
                    CourseProgress courseProgress = courseProgressRepository.findByCourseIdAndUserId(objectId, userId);
                    if (courseProgress != null) {
                        return ProgressResponse.builder()
                                .id(courseProgress.getId())
                                .isCompleted(courseProgress.getIsCompleted())
                                .objectId(objectId)
                                .objectName("Course Progress")
                                .progress(courseProgress.getProgress())
                                .userId(userId)
                                .build();
                    }
                    return null;
                }
                case "LEVEL" -> {
                    LevelProgress levelProgress = levelProgressRepository.findByLevelIdAndUserId(objectId, userId);
                    if (levelProgress != null) {
                        return ProgressResponse.builder()
                                .id(levelProgress.getId())
                                .isCompleted(levelProgress.getIsCompleted())
                                .objectId(objectId)
                                .objectName("Level Progress")
                                .progress(levelProgress.getProgress())
                                .userId(userId)
                                .build();
                    }
                    return null;
                }
                case "CHAPTER" -> {
                    ChapterProgress chapterProgress = chapterProgressRepository.findByChapterIdAndUserId(objectId, userId);
                    if (chapterProgress != null) {
                        return ProgressResponse.builder()
                                .id(chapterProgress.getId())
                                .isCompleted(chapterProgress.getIsCompleted())
                                .objectId(objectId)
                                .objectName("Chapter Progress")
                                .progress(chapterProgress.getProgress())
                                .userId(userId)
                                .build();
                    }
                    return null;
                }
                case "LESSON" -> {
                    LessonProgress lessonProgress = lessonProgressRepository.findByLessonIdAndUserId(objectId, userId);
                    if (lessonProgress != null) {
                        return ProgressResponse.builder()
                                .id(lessonProgress.getId())
                                .isCompleted(lessonProgress.getIsCompleted())
                                .objectId(objectId)
                                .objectName("Lesson Progress")
                                .progress(lessonProgress.getProgress())
                                .userId(userId)
                                .build();
                    }
                    return null;
                }
                case "GRAMMAR" -> {
                    GrammarProgress grammarProgress = grammarProgressRepository.findByGrammarIdAndUserId(objectId, userId);
                    if (grammarProgress != null) {
                        return ProgressResponse.builder()
                                .id(grammarProgress.getId())
                                .isCompleted(grammarProgress.getIsCompleted())
                                .objectId(objectId)
                                .objectName("Grammar Progress")
                                .progress(grammarProgress.getProgress())
                                .userId(userId)
                                .build();
                    }
                    return null;
                }
                case "GRAMMAR_SECTION" -> {
                    GrammarSectionProgress grammarSectionProgress = grammarSectionProgressRepository.findByGrammarSectionIdAndUserId(objectId, userId);
                    if (grammarSectionProgress != null) {
                        return ProgressResponse.builder()
                                .id(grammarSectionProgress.getId())
                                .isCompleted(grammarSectionProgress.getIsCompleted())
                                .objectId(objectId)
                                .objectName("Grammar Section Progress")
                                .progress(grammarSectionProgress.getProgress())
                                .userId(userId)
                                .build();
                    }
                    return null;
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
