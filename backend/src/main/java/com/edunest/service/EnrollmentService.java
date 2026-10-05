package com.edunest.service;

import com.edunest.dto.response.CourseResponse;
import com.edunest.entity.Course;
import com.edunest.entity.Enrollment;
import com.edunest.entity.User;
import com.edunest.repository.CourseRepository;
import com.edunest.repository.EnrollmentRepository;
import com.edunest.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseService courseService;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             UserRepository userRepository,
                             CourseService courseService) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseService = courseService;
    }

    @Transactional
    public void enroll(Long courseId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.isPublished()) {
            throw new RuntimeException("Cannot enroll in an unpublished course");
        }

        if (enrollmentRepository.existsByUserIdAndCourseId(student.getId(), courseId)) {
            return; // Idempotent if already enrolled
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setUser(student);
        enrollment.setCourse(course);
        enrollment.setDiagnosticCompleted(false);
        enrollmentRepository.save(enrollment);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getMyEnrolledCourses(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return enrollmentRepository.findByUserId(student.getId()).stream()
                .map(Enrollment::getCourse)
                .map(courseService::toCourseResponse)
                .toList();
    }
}
