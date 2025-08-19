package com.busuu.app.services.lesson;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.lesson.LessonDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.LessonResponse;
import com.busuu.app.entities.*;
import com.busuu.app.entities.progresses.LessonProgress;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.ChapterRepository;
import com.busuu.app.repositories.LessonRepository;
import com.busuu.app.repositories.progress.LessonProgressRepository;
import com.busuu.app.services.cloudinary.UploadCloudinaryService;
import com.busuu.app.services.word.WordService;
import com.busuu.app.utils.UploadCloudinaryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonService implements ILessonService {

    private final LessonRepository lessonRepository;
    private final ChapterRepository chapterRepository;
    private final WordService wordService;
    private final UploadCloudinaryService uploadCloudinaryService;
    private final LessonProgressRepository lessonProgressRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public LessonResponse insertLesson(String requestId, LessonDTO lessonDTO) {
        try {

            Chapter existingChapter = chapterRepository.findById(lessonDTO.getChapterId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Chapter with ID = " + lessonDTO.getChapterId()));

            if (lessonRepository.existsByTitleAndChapterId(lessonDTO.getTitle(), existingChapter.getId())) {
                throw new ExistDataException("Lesson's title is duplicated");
            }

//            if (lessonRepository.existsByLessonOrderAndChapterId(lessonDTO.getLessonOrder(), existingChapter.getId())) {
//                throw new ExistDataException("Lesson's order is duplicated");
//            }

            Lesson lesson = modelMapper.map(lessonDTO, Lesson.class);
            lesson.setId(UUID.randomUUID().toString());

            if (lessonDTO.getFlagIcon() != null) {
                CloudinaryResponse cloudinaryResponse = uploadFlagIcon(lessonDTO.getFlagIcon());

                if (cloudinaryResponse != null) {
                    lesson.setFlagIconName(cloudinaryResponse.getPublicId());
                    lesson.setFlagIconUrl(cloudinaryResponse.getUrl());
                }
            }
            lesson.setChapter(existingChapter);

            // Set lesson order max + 1
            Integer lessonOrder = lessonRepository.findMaxGrammarSectionOrderByChapterId(lessonDTO.getChapterId());
            if (lessonOrder == null) {
                lesson.setLessonOrder(1);
            } else {
                lesson.setLessonOrder(lessonOrder + 1);
            }

            lesson = lessonRepository.save(lesson);

            // Save word
            Lesson finalLesson = lesson;

            lessonDTO.getWords().stream()
                    .filter(Objects::nonNull)
                    .forEach(wordDTO -> {
                        wordDTO.setLessonId(finalLesson.getId());
                        wordService.createWord(requestId, wordDTO);
                    });

            LessonResponse lessonResponse = modelMapper.map(lesson, LessonResponse.class);
            lessonResponse.setChapterId(lesson.getChapter().getId());

            return lessonResponse;
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to create lesson, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_LESSON, requestId);
        }
    }

    @Override
    public LessonResponse getLesson(String requestId, String lessonId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            Lesson existingLesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + lessonId));

            LessonProgress lessonProgress = lessonProgressRepository.findByLessonIdAndUserId(lessonId, userId);

            LessonResponse lessonResponse = modelMapper.map(existingLesson, LessonResponse.class);
            lessonResponse.setChapterId(existingLesson.getChapter().getId());
            if (lessonProgress != null) {
                lessonResponse.setProgress(lessonProgress.getProgress());
                lessonResponse.setIsCompleted(lessonProgress.getIsCompleted());
            }
            return lessonResponse;
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get lesson, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LESSON, requestId);
        }
    }

    @Override
    public Page<LessonResponse> getLessons(String requestId, int page, int size, String sortBy, String sortDirection) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            //Pageable - NativeQuery
            Sort sort = Sort.by(
                    Sort.Order.by(sortBy).with(Sort.Direction.fromString(sortDirection)),
                    Sort.Order.by("lesson_id").with(Sort.Direction.fromString(sortDirection))
            );
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<Lesson> lessons = lessonRepository.findAll(pageable);

            return lessons.map(lesson -> {
                LessonProgress lessonProgress = lessonProgressRepository.findByLessonIdAndUserId(lesson.getId(), userId);

                LessonResponse lessonResponse = modelMapper.map(lesson, LessonResponse.class);
                lessonResponse.setChapterId(lesson.getChapter().getId());

                if (lessonProgress != null) {
                    lessonResponse.setProgress(lessonProgress.getProgress());
                    lessonResponse.setIsCompleted(lessonProgress.getIsCompleted());
                }
                return lessonResponse;
            });

        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get lessons, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LESSON, requestId);
        }
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(String requestId, String lessonId, LessonDTO lessonDTO) {
        try {
            Lesson existingLesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + lessonId));

            if (!existingLesson.getChapter().getId().equals(lessonDTO.getChapterId())) {
                Chapter existingChapter = chapterRepository.findById(lessonDTO.getChapterId())
                        .orElseThrow(() -> new DataNotFoundException("Cannot find Chapter with ID = " + lessonDTO.getChapterId()));

                existingLesson.setChapter(existingChapter);
            }

            if (!existingLesson.getTitle().equals(lessonDTO.getTitle())) {
                if (lessonRepository.existsByTitleAndChapterId(lessonDTO.getTitle(), existingLesson.getChapter().getId())) {
                    throw new ExistDataException("Lesson's title is duplicated");
                }
            }

            if (!Objects.equals(existingLesson.getLessonOrder(), lessonDTO.getLessonOrder())) {
                if (lessonRepository.existsByLessonOrderAndChapterId(lessonDTO.getLessonOrder(), existingLesson.getChapter().getId())) {
                    throw new ExistDataException("Lesson's order is duplicated");
                }
            }

            if (lessonDTO.getFlagIcon() != null) {
                boolean isRemove = true;
                if (existingLesson.getFlagIconName() != null) {
                    isRemove = uploadCloudinaryService.removeFile(existingLesson.getFlagIconName());
                }
                if (isRemove) {
                    CloudinaryResponse cloudinaryResponse = uploadFlagIcon(lessonDTO.getFlagIcon());
                    if (cloudinaryResponse != null) {
                        existingLesson.setFlagIconUrl(cloudinaryResponse.getUrl());
                        existingLesson.setFlagIconName(cloudinaryResponse.getPublicId());
                    }
                }
            }

            modelMapper.map(lessonDTO, existingLesson);
            existingLesson = lessonRepository.save(existingLesson);

            LessonResponse lessonResponse = modelMapper.map(existingLesson, LessonResponse.class);
            lessonResponse.setChapterId(existingLesson.getChapter().getId());

            return lessonResponse;
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to update lesson, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_LESSON, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteLesson(String requestId, String lessonId) {
        try {
            Lesson existingLesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + lessonId));

            uploadCloudinaryService.removeFile(existingLesson.getFlagIconName());

            lessonRepository.deleteById(lessonId);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete lesson, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_LESSON, requestId);
        }
    }

    @Override
    public List<LessonResponse> getByChapterId(String requestId, String chapterId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) auth.getPrincipal();
            String userId = user.getId();

            List<Lesson> lessons = lessonRepository.findByChapterId(chapterId, Sort.by(Sort.Direction.ASC, "lessonOrder"));

            return lessons.stream().map(lesson -> {
                LessonProgress lessonProgress = lessonProgressRepository.findByLessonIdAndUserId(lesson.getId(), userId);

                LessonResponse lessonResponse = modelMapper.map(lesson, LessonResponse.class);
                lessonResponse.setChapterId(lesson.getChapter().getId());

                if (lessonProgress != null) {
                    lessonResponse.setProgress(lessonProgress.getProgress());
                    lessonResponse.setIsCompleted(lessonProgress.getIsCompleted());
                }
                return lessonResponse;
            }).toList();
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get lessons, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LESSON, requestId);
        }
    }

    private CloudinaryResponse uploadFlagIcon(MultipartFile file) throws Exception {
        UploadCloudinaryUtil.assertAllowed(file, "image");
        String fileName = UploadCloudinaryUtil.getFileName(file.getOriginalFilename());
        CloudinaryResponse response = uploadCloudinaryService.uploadFile(file, fileName, "image");
        return response;
    }
}
