package com.busuu.app.services.course;

import com.busuu.app.configs.constant.Constants;
import com.busuu.app.dtos.requests.course.CourseDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.CourseResponse;
import com.busuu.app.entities.Course;
import com.busuu.app.entities.CourseLevel;
import com.busuu.app.entities.Level;
import com.busuu.app.exceptions.DataNotFoundException;
import com.busuu.app.exceptions.ErrorHandleException;
import com.busuu.app.exceptions.ExistDataException;
import com.busuu.app.repositories.course.CourseLevelRepository;
import com.busuu.app.repositories.course.CourseRepository;
import com.busuu.app.repositories.LevelRepository;
import com.busuu.app.services.cloudinary.IUploadCloudinaryService;
import com.busuu.app.utils.UploadCloudinaryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService implements ICourseService {

    private final CourseRepository courseRepository;
    private final CourseLevelRepository courseLevelRepository;
    private final LevelRepository levelRepository;
    private final ModelMapper modelMapper;
    private final IUploadCloudinaryService uploadCloudinaryService;

    @Override
    @Transactional
    public CourseResponse insertCourse(String requestId, CourseDTO courseDTO) {
        try {
            if (courseRepository.existsByTitle(courseDTO.getTitle())) {
                throw new ExistDataException("Course's title is duplicated");
            }

//            if (courseRepository.existsByCourseOrder(courseDTO.getCourseOrder())) {
//                throw new ExistDataException("Course's order is duplicated");
//            }

            CloudinaryResponse cloudinaryResponse = null;
            if (courseDTO.getFlagIcon() != null) {
                cloudinaryResponse = uploadFlagIcon(courseDTO.getFlagIcon());
            }

            Course course = modelMapper.map(courseDTO, Course.class);
            course.setId(UUID.randomUUID().toString());

            // Set course order max + 1
            course.setCourseOrder(courseRepository.findMaxCourseOrder() + 1);

            if (cloudinaryResponse != null) {
                course.setFlagIconUrl(cloudinaryResponse.getUrl());
                course.setFlagIconName(cloudinaryResponse.getPublicId());
            }

            course = courseRepository.save(course);

            List<String> level = new ArrayList<>();

            List<String> levelIds = Arrays.stream(courseDTO.getLevelIds().split(",")).toList();
            for (String levelId : levelIds) {
                try {
                    Level existingLevel = levelRepository.findById(levelId)
                            .orElseThrow(() -> new DataNotFoundException("Cannot find Level with ID = " + levelId));

                    if (courseLevelRepository.existsByCourseIdAndLevelId(course.getId(), existingLevel.getId())) {
                        continue;
                    }

                    CourseLevel courseLevel = CourseLevel.builder()
                            .id(UUID.randomUUID().toString())
                            .course(course)
                            .level(existingLevel)
                            .build();

                    courseLevel = courseLevelRepository.save(courseLevel);
                    level.add(existingLevel.getCode());
                } catch (DataNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            CourseResponse courseResponse = modelMapper.map(course, CourseResponse.class);
            courseResponse.setLevelIds(level);
            return courseResponse;
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to create course, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_CREATE_COURSE, requestId);
        }
    }

    @Override
    public CourseResponse getCourse(String requestId, String courseId) {
        try {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Course with ID = " + courseId));

            List<CourseLevel> courseLevels = courseLevelRepository.findByCourseIdWithSortingLevel(courseId);

            List<String> levels = courseLevels.stream().map(
                    courseLevel -> courseLevel.getLevel().getId()
            ).toList();

            CourseResponse courseResponse = modelMapper.map(course, CourseResponse.class);
            courseResponse.setLevelIds(levels);
            return courseResponse;
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get course, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_COURSE, requestId);
        }
    }

    @Override
    public List<CourseResponse> getCourses(String requestId) {
        try {
            List<Course> courses = courseRepository.findAll();

            return courses.stream().map(
                    course -> {
                        List<CourseLevel> courseLevels = courseLevelRepository.findByCourseIdWithSortingLevel(course.getId());

                        List<String> levels = courseLevels.stream().map(
                                courseLevel -> courseLevel.getLevel().getId()
                        ).toList();

                        CourseResponse courseResponse = modelMapper.map(course, CourseResponse.class);
                        courseResponse.setLevelIds(levels);
                        return courseResponse;
                    }
            ).toList();
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to get courses, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_GET_COURSE, requestId);
        }
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(String requestId, String courseId, CourseDTO courseDTO) {
        try {
            Course existingCourse = courseRepository.findById(courseId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find Course with ID = " + courseId));

            if (!existingCourse.getTitle().equals(courseDTO.getTitle())) {
                if (courseRepository.existsByTitle(courseDTO.getTitle())) {
                    throw new ExistDataException("Course's title is duplicated");
                }
            }

            if (!Objects.equals(existingCourse.getCourseOrder(), courseDTO.getCourseOrder())) {
                if (courseRepository.existsByCourseOrder(courseDTO.getCourseOrder())) {
                    throw new ExistDataException("Course's order is duplicated");
                }
            }

            if (courseDTO.getFlagIcon() != null) {
                boolean isRemove = true;
                if (existingCourse.getFlagIconName() != null) {
                    isRemove = uploadCloudinaryService.removeFile(existingCourse.getFlagIconName());
                }
                if (isRemove) {
                    CloudinaryResponse cloudinaryResponse = uploadFlagIcon(courseDTO.getFlagIcon());
                    if (cloudinaryResponse != null) {
                        existingCourse.setFlagIconUrl(cloudinaryResponse.getUrl());
                        existingCourse.setFlagIconName(cloudinaryResponse.getPublicId());
                    }
                }
            }

            modelMapper.map(courseDTO, existingCourse);
            existingCourse = courseRepository.save(existingCourse);

            courseLevelRepository.deleteByCourseId(existingCourse.getId());

            List<String> level = new ArrayList<>();

            List<String> levelIds = Arrays.stream(courseDTO.getLevelIds().split(",")).toList();
            for (String levelId : levelIds) {
                try {
                    Level existingLevel = levelRepository.findById(levelId)
                            .orElseThrow(() -> new DataNotFoundException("Cannot find Level with ID = " + levelId));

                    if (courseLevelRepository.existsByCourseIdAndLevelId(existingCourse.getId(), existingLevel.getId())) {
                        continue;
                    }

                    CourseLevel courseLevel = CourseLevel.builder()
                            .id(UUID.randomUUID().toString())
                            .course(existingCourse)
                            .level(existingLevel)
                            .build();

                    courseLevel = courseLevelRepository.save(courseLevel);
                    level.add(existingLevel.getId());
                } catch (DataNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            CourseResponse courseResponse = modelMapper.map(existingCourse, CourseResponse.class);
            courseResponse.setLevelIds(level);
            return courseResponse;
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to update course, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_UPDATE_COURSE, requestId);
        }
    }

    @Override
    @Transactional
    public void deleteCourse(String requestId, String courseId) {
        try {
            Course existingCourse = courseRepository.findById(courseId)
                            .orElseThrow(() -> new DataNotFoundException("Cannot find Course with ID = " + courseId));

            uploadCloudinaryService.removeFile(existingCourse.getFlagIconName());

            courseLevelRepository.deleteByCourseId(courseId);

            courseRepository.deleteById(courseId);
        } catch (Exception e) {
            log.error("requestId="+requestId+",failed to delete course, err="+e.getMessage());
            throw new ErrorHandleException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR,
                    Constants.ERROR_CODE.ERR_DELETE_COURSE, requestId);
        }
    }

    private CloudinaryResponse uploadFlagIcon(MultipartFile file) throws Exception {
        UploadCloudinaryUtil.assertAllowed(file, "image");
        String fileName = UploadCloudinaryUtil.getFileName(file.getOriginalFilename());
        CloudinaryResponse response = uploadCloudinaryService.uploadFile(file, fileName, "image");
        return response;
    }
}
