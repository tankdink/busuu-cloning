package com.busuu.app.services.course;

import com.busuu.app.dtos.requests.course.CourseDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ICourseService {

    CourseResponse insertCourse (String requestId, CourseDTO courseDTO);

    CourseResponse getCourse (String requestId, String courseId);

    Page<CourseResponse> getCourses (String requestId, int page, int size, List<String> sortBy, List<String> sortDirection, String searchValue, String level, String language);

    CourseResponse updateCourse (String requestId, String courseId, CourseDTO courseDTO);

    void deleteCourse (String requestId, String courseId);
}
