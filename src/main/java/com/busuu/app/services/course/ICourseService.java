package com.busuu.app.services.course;

import com.busuu.app.dtos.requests.course.CourseDTO;
import com.busuu.app.dtos.responses.CloudinaryResponse;
import com.busuu.app.dtos.responses.CourseResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ICourseService {

    CourseResponse insertCourse (String requestId, CourseDTO courseDTO);

    CourseResponse getCourse (String requestId, String courseId);

    List<CourseResponse> getCourses (String requestId);

    CourseResponse updateCourse (String requestId, String courseId, CourseDTO courseDTO);

    void deleteCourse (String requestId, String courseId);
}
