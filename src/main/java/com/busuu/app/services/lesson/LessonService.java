package com.busuu.app.services.lesson;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.lesson.LessonDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.CourseResponse;
import com.busuu.app.dtos.responses.LessonResponse;
import com.busuu.app.entities.*;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.ChapterRepository;
import com.busuu.app.repositories.LessonRepository;
import com.busuu.app.services.cloudinary.UploadCloudinaryService;
import com.busuu.app.utils.UploadCloudinaryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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
    private final UploadCloudinaryService uploadCloudinaryService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public LessonResponse insertLesson(String requestId, LessonDTO lessonDTO) {
        try {

            if (lessonRepository.existsByTitle(lessonDTO.getTitle())) {
                throw new ExistDataException("Lesson's title is duplicated");
            }

            if (lessonRepository.existsByLessonOrder(lessonDTO.getLessonOrder())) {
                throw new ExistDataException("Lesson's order is duplicated");
            }

            Chapter existingChapter = chapterRepository.findById(lessonDTO.getChapterId())
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Chapter with ID = " + lessonDTO.getChapterId()));

            Lesson lesson = modelMapper.map(lessonDTO, Lesson.class);

            if (lessonDTO.getFlagIcon() != null) {
                CloudinaryResponse cloudinaryResponse = uploadFlagIcon(lessonDTO.getFlagIcon());

                if (cloudinaryResponse != null) {
                    lesson.setFlagIconName(cloudinaryResponse.getPublicId());
                    lesson.setFlagIconUrl(cloudinaryResponse.getUrl());
                }
            }
            lesson.setChapter(existingChapter);

            lesson = lessonRepository.save(lesson);

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
            Lesson existingLesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Lesson with ID = " + lessonId));

            LessonResponse lessonResponse = modelMapper.map(existingLesson, LessonResponse.class);
            lessonResponse.setChapterId(existingLesson.getChapter().getId());

            return lessonResponse;
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get lesson, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_LESSON, requestId);
        }
    }

    @Override
    public List<LessonResponse> getLessons(String requestId) {
        try {
            List<Lesson> lessons = lessonRepository.findAll();

            return lessons.stream().map(lesson -> {
                LessonResponse lessonResponse = modelMapper.map(lesson, LessonResponse.class);
                lessonResponse.setChapterId(lesson.getChapter().getId());
                return lessonResponse;
            }).toList();
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
                if (lessonRepository.existsByTitle(lessonDTO.getTitle())) {
                    throw new ExistDataException("Course's title is duplicated");
                }
            }

            if (!Objects.equals(existingLesson.getLessonOrder(), lessonDTO.getLessonOrder())) {
                if (lessonRepository.existsByLessonOrder(lessonDTO.getLessonOrder())) {
                    throw new ExistDataException("Course's order is duplicated");
                }
            }

            if (lessonDTO.getFlagIcon() != null) {
                boolean isRemove = uploadCloudinaryService.removeFile(existingLesson.getFlagIconName());
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

    private CloudinaryResponse uploadFlagIcon(MultipartFile file) throws Exception {
        UploadCloudinaryUtil.assertAllowed(file, "image");
        String fileName = UploadCloudinaryUtil.getFileName(file.getOriginalFilename());
        CloudinaryResponse response = uploadCloudinaryService.uploadFile(file, fileName, "image");
        return response;
    }
}
