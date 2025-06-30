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
import com.busuu.app.services.progress.chapter.IChapterProgressService;
import com.busuu.app.services.progress.course.ICourseProgressService;
import com.busuu.app.services.progress.grammar.IGrammarProgressService;
import com.busuu.app.services.progress.grammar_section.IGrammarSectionProgressService;
import com.busuu.app.services.progress.lesson.ILessonProgressService;
import com.busuu.app.services.progress.level.ILevelProgressService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressService implements IProgressService {

    // Repository Progress
//    private final CourseProgressRepository courseProgressRepository;
//    private final LevelProgressRepository levelProgressRepository;
//    private final ChapterProgressRepository chapterProgressRepository;
//    private final LessonProgressRepository lessonProgressRepository;
//    private final GrammarProgressRepository grammarProgressRepository;
//    private final GrammarSectionProgressRepository grammarSectionProgressRepository;

    private final ICourseProgressService courseProgressService;
    private final ILevelProgressService levelProgressService;
    private final IChapterProgressService chapterProgressService;
    private final ILessonProgressService lessonProgressService;
    private final IGrammarProgressService grammarProgressService;
    private final IGrammarSectionProgressService grammarSectionProgressService;

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

            // Validate constraints Course – Level – Chapter – Lesson
            validateCourseStructure(progressDTO);

            // Find lesson
            Lesson lesson = lessonRepository.findById(progressDTO.getLessonId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + progressDTO.getLessonId()));

            if (lesson.getQuestions().isEmpty()) {
                throw new DataNotFoundException("Cannot find list Question with Lesson ID = " + progressDTO.getLessonId());
            }

            // UPSERT

            // LessonProgress
            LessonProgress lessonProgress = lessonProgressService.upsertLessonProgress(lesson, existingUser, progressDTO.getNumberQuestions());

            // ChapterProgress
            Chapter chapter = chapterRepository.findById(progressDTO.getChapterId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Chapter with ID = " + progressDTO.getChapterId()));
            ChapterProgress chapterProgress = chapterProgressService.upsertChapterProgress(chapter, existingUser);

            // LevelProgress
            Level level = levelRepository.findById(progressDTO.getLevelId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Level with ID = " + progressDTO.getLevelId()));
            LevelProgress levelProgress = levelProgressService.upsertLevelProgress(level, existingUser);

            // CourseProgress
            Course course = courseRepository.findById(progressDTO.getCourseId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Course with ID = " + progressDTO.getCourseId()));
            CourseProgress courseProgress = courseProgressService.upsertCourseProgress(course, existingUser);

            // Response
            return ProgressResponse.builder()
                    .userId(userId)
                    .objectName("Lesson Progress")
                    .progress(lessonProgress.getProgress())
                    .objectId(lesson.getId())
                    .isCompleted(lessonProgress.getIsCompleted())
                    .id(lessonProgress.getId())
                    .build();

        } catch (Exception e) {
            log.error("requestId={}, failed to upsert object progress", requestId, e);
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPSERT_PROGRESS, requestId);
        }
    }

    private void validateCourseStructure(CourseProgressDTO dto) {
        boolean courseLevelExists = chapterRepository.existsByCourseIdAndLevelId(dto.getCourseId(), dto.getLevelId());
        if (!courseLevelExists) {
            throw new DataNotFoundException("Cannot find Chapter ID = " + dto.getChapterId() +
                    " of Level ID = " + dto.getLevelId() + " of Course ID = " + dto.getCourseId());
        }

        boolean lessonInChapter = lessonRepository.existsByIdAndChapterId(dto.getLessonId(), dto.getChapterId());
        if (!lessonInChapter) {
            throw new DataNotFoundException("Cannot find Lesson ID = " + dto.getLessonId() +
                    " of Chapter ID = " + dto.getChapterId());
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

            // Validate Grammar structure
            validateGrammarStructure(progressDTO);

            // GrammarSection
            GrammarSection section = grammarSectionRepository.findById(progressDTO.getGrammarSectionId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Grammar Section with ID = " + progressDTO.getGrammarSectionId()));

            // UPSERT
            // GrammarSectionProgress
            GrammarSectionProgress grammarSectionProgress = grammarSectionProgressService.upsertGrammarSectionProgress(section, existingUser, progressDTO.getNumberQuestions());

            //  GrammarProgress
            Grammar grammar = grammarRepository.findById(progressDTO.getGrammarId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Grammar with ID = " + progressDTO.getGrammarId()));
            GrammarProgress grammarProgress = grammarProgressService.upsertGrammarProgress(grammar, existingUser);

            // Trả về response
            return ProgressResponse.builder()
                    .userId(userId)
                    .objectId(section.getId())
                    .objectName("Grammar Section Progress")
                    .progress(grammarSectionProgress.getProgress())
                    .isCompleted(grammarSectionProgress.getIsCompleted())
                    .id(grammarSectionProgress.getId())
                    .build();

        } catch (Exception e) {
            log.error("requestId={}, failed to upsert grammar progress", requestId, e);
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPSERT_PROGRESS, requestId);
        }
    }

    private void validateGrammarStructure(GrammarProgressDTO dto) {
        boolean exists = grammarSectionRepository.existsGrammarSectionByGrammarId(dto.getGrammarId());
        if (!exists) {
            throw new DataNotFoundException("Cannot find Grammar Section with ID = " + dto.getGrammarSectionId()
                    + " of Grammar ID = " + dto.getGrammarId());
        }

        boolean sectionExists = grammarSectionRepository.existsById(dto.getGrammarSectionId());
        if (!sectionExists) {
            throw new DataNotFoundException("Cannot find Grammar Section with ID = " + dto.getGrammarSectionId());
        }
    }


//    @Override
//    public ProgressResponse getObjectProgress(String requestId, String objectId, String userId, String objectName) {
//        try {
//            switch (objectName) {
//                case "COURSE" -> {
//                    CourseProgress courseProgress = courseProgressRepository.findByCourseIdAndUserId(objectId, userId);
//                    if (courseProgress != null) {
//                        return ProgressResponse.builder()
//                                .id(courseProgress.getId())
//                                .isCompleted(courseProgress.getIsCompleted())
//                                .objectId(objectId)
//                                .objectName("Course Progress")
//                                .progress(courseProgress.getProgress())
//                                .userId(userId)
//                                .build();
//                    }
//                    return null;
//                }
//                case "LEVEL" -> {
//                    LevelProgress levelProgress = levelProgressRepository.findByLevelIdAndUserId(objectId, userId);
//                    if (levelProgress != null) {
//                        return ProgressResponse.builder()
//                                .id(levelProgress.getId())
//                                .isCompleted(levelProgress.getIsCompleted())
//                                .objectId(objectId)
//                                .objectName("Level Progress")
//                                .progress(levelProgress.getProgress())
//                                .userId(userId)
//                                .build();
//                    }
//                    return null;
//                }
//                case "CHAPTER" -> {
//                    ChapterProgress chapterProgress = chapterProgressRepository.findByChapterIdAndUserId(objectId, userId);
//                    if (chapterProgress != null) {
//                        return ProgressResponse.builder()
//                                .id(chapterProgress.getId())
//                                .isCompleted(chapterProgress.getIsCompleted())
//                                .objectId(objectId)
//                                .objectName("Chapter Progress")
//                                .progress(chapterProgress.getProgress())
//                                .userId(userId)
//                                .build();
//                    }
//                    return null;
//                }
//                case "LESSON" -> {
//                    LessonProgress lessonProgress = lessonProgressRepository.findByLessonIdAndUserId(objectId, userId);
//                    if (lessonProgress != null) {
//                        return ProgressResponse.builder()
//                                .id(lessonProgress.getId())
//                                .isCompleted(lessonProgress.getIsCompleted())
//                                .objectId(objectId)
//                                .objectName("Lesson Progress")
//                                .progress(lessonProgress.getProgress())
//                                .userId(userId)
//                                .build();
//                    }
//                    return null;
//                }
//                case "GRAMMAR" -> {
//                    GrammarProgress grammarProgress = grammarProgressRepository.findByGrammarIdAndUserId(objectId, userId);
//                    if (grammarProgress != null) {
//                        return ProgressResponse.builder()
//                                .id(grammarProgress.getId())
//                                .isCompleted(grammarProgress.getIsCompleted())
//                                .objectId(objectId)
//                                .objectName("Grammar Progress")
//                                .progress(grammarProgress.getProgress())
//                                .userId(userId)
//                                .build();
//                    }
//                    return null;
//                }
//                case "GRAMMAR_SECTION" -> {
//                    GrammarSectionProgress grammarSectionProgress = grammarSectionProgressRepository.findByGrammarSectionIdAndUserId(objectId, userId);
//                    if (grammarSectionProgress != null) {
//                        return ProgressResponse.builder()
//                                .id(grammarSectionProgress.getId())
//                                .isCompleted(grammarSectionProgress.getIsCompleted())
//                                .objectId(objectId)
//                                .objectName("Grammar Section Progress")
//                                .progress(grammarSectionProgress.getProgress())
//                                .userId(userId)
//                                .build();
//                    }
//                    return null;
//                }
//                default -> throw new ExistDataException("Object name is not valid");
//            }
//        } catch (Exception e) {
//            log.error("requestId="+requestId+",failed to get object progress, err="+e.getMessage());
//            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
//                    Constants.ERROR_CODE.ERR_GET_PROGRESS, requestId);
//        }
//    }
}
