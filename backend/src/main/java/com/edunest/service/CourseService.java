package com.edunest.service;

import com.edunest.dto.request.CreateCourseRequest;
import com.edunest.dto.request.CreateLessonRequest;
import com.edunest.dto.request.CreateTopicRequest;
import com.edunest.dto.response.CourseDetailResponse;
import com.edunest.dto.response.CourseResponse;
import com.edunest.dto.response.LessonResponse;
import com.edunest.dto.response.TopicResponse;
import com.edunest.entity.*;
import com.edunest.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final MasteryService masteryService;
    private final CertificateService certificateService;

    public CourseService(CourseRepository courseRepository,
                         TopicRepository topicRepository,
                         LessonRepository lessonRepository,
                         EnrollmentRepository enrollmentRepository,
                         UserRepository userRepository,
                         LessonProgressRepository lessonProgressRepository,
                         MasteryService masteryService,
                         CertificateService certificateService) {
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.lessonRepository = lessonRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.lessonProgressRepository = lessonProgressRepository;
        this.masteryService = masteryService;
        this.certificateService = certificateService;
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllPublishedCourses() {
        return courseRepository.findByPublishedTrue().stream()
                .map(this::toCourseResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getInstructorCourses(String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));
        return courseRepository.findByInstructorId(instructor.getId()).stream()
                .map(this::toCourseResponse)
                .toList();
    }

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest req, String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Course course = new Course();
        course.setTitle(req.getTitle().trim());
        course.setDescription(req.getDescription());
        course.setThumbnailUrl(req.getThumbnailUrl());
        course.setInstructor(instructor);
        course.setPublished(false);

        Course saved = courseRepository.save(course);
        return toCourseResponse(saved);
    }

    @Transactional
    public TopicResponse addTopic(Long courseId, CreateTopicRequest req, String instructorEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getEmail().equalsIgnoreCase(instructorEmail)) {
            throw new RuntimeException("Not authorized to modify this course");
        }

        Topic topic = new Topic();
        topic.setCourse(course);
        topic.setTitle(req.getTitle().trim());
        topic.setDescription(req.getDescription());
        topic.setOrderIndex(req.getOrderIndex());

        Topic saved = topicRepository.save(topic);
        return toTopicResponse(saved);
    }

    @Transactional
    public LessonResponse addLesson(Long topicId, CreateLessonRequest req, String instructorEmail) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getInstructor().getEmail().equalsIgnoreCase(instructorEmail)) {
            throw new RuntimeException("Not authorized to modify this topic");
        }

        Lesson lesson = new Lesson();
        lesson.setTopic(topic);
        lesson.setTitle(req.getTitle().trim());
        lesson.setContent(req.getContent());
        lesson.setKeyPoints(req.getKeyPoints());
        lesson.setEstimatedMinutes(req.getEstimatedMinutes() > 0 ? req.getEstimatedMinutes() : 15);
        lesson.setOrderIndex(req.getOrderIndex());

        Lesson saved = lessonRepository.save(lesson);
        return toLessonResponse(saved);
    }

    @Transactional
    public CourseResponse publishCourse(Long courseId, String instructorEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getInstructor().getEmail().equalsIgnoreCase(instructorEmail)) {
            throw new RuntimeException("Not authorized to publish this course");
        }

        course.setPublished(true);
        Course saved = courseRepository.save(course);
        return toCourseResponse(saved);
    }

    @Transactional(readOnly = true)
    public CourseDetailResponse getCourseDetail(Long courseId, String currentEmail) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        boolean enrolled = false;
        boolean diagnosticCompleted = false;
        boolean courseCompleted = false;
        LocalDateTime courseCompletedAt = null;
        Double finalScore = null;
        boolean finalAssessmentPassed = false;
        int completedLessonsCount = 0;

        List<Lesson> allLessons = lessonRepository.findByTopicCourseIdOrderByTopicOrderIndexAscOrderIndexAsc(courseId);
        int totalLessonsCount = allLessons.size();

        if (currentEmail != null) {
            Optional<User> userOpt = userRepository.findByEmail(currentEmail);
            if (userOpt.isPresent()) {
                Long userId = userOpt.get().getId();
                Optional<Enrollment> enrollmentOpt =
                        enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
                if (enrollmentOpt.isPresent()) {
                    Enrollment enrollment = enrollmentOpt.get();
                    enrolled = true;
                    diagnosticCompleted = enrollment.isDiagnosticCompleted();
                    courseCompleted = enrollment.isCompleted();
                    courseCompletedAt = enrollment.getCompletedAt();
                    finalScore = enrollment.getFinalScore();
                    finalAssessmentPassed = enrollment.isFinalAssessmentPassed();
                }
                completedLessonsCount = lessonProgressRepository
                        .findByUserIdAndLessonTopicCourseIdAndCompletedTrue(userId, courseId).size();
            }
        }

        boolean finalAssessmentUnlocked = (totalLessonsCount > 0 && completedLessonsCount >= totalLessonsCount);

        List<TopicResponse> topics = topicRepository.findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream()
                .map(this::toTopicResponse)
                .toList();

        CourseDetailResponse detail = new CourseDetailResponse();
        detail.setId(course.getId());
        detail.setTitle(course.getTitle());
        detail.setDescription(course.getDescription());
        detail.setThumbnailUrl(course.getThumbnailUrl());
        detail.setInstructorName(course.getInstructor().getName());
        detail.setPublished(course.isPublished());
        detail.setEnrolled(enrolled);
        detail.setDiagnosticCompleted(diagnosticCompleted);
        detail.setTopics(topics);

        detail.setCourseCompleted(courseCompleted);
        detail.setCourseCompletedAt(courseCompletedAt);
        detail.setFinalScore(finalScore);
        detail.setFinalAssessmentPassed(finalAssessmentPassed);
        detail.setFinalAssessmentUnlocked(finalAssessmentUnlocked);
        detail.setCompletedLessonsCount(completedLessonsCount);
        detail.setTotalLessonsCount(totalLessonsCount);
        detail.setFinalAssessmentPassingScore(70.0);

        return detail;
    }

    @Transactional(readOnly = true)
    public LessonResponse getLessonById(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        LessonResponse dto = toLessonResponse(lesson);

        // Compute previous and next lesson IDs across the course
        Long courseId = lesson.getTopic().getCourse().getId();
        List<Lesson> allCourseLessons =
                lessonRepository.findByTopicCourseIdOrderByTopicOrderIndexAscOrderIndexAsc(courseId);

        for (int i = 0; i < allCourseLessons.size(); i++) {
            if (allCourseLessons.get(i).getId().equals(lessonId)) {
                if (i > 0) {
                    dto.setPreviousLessonId(allCourseLessons.get(i - 1).getId());
                }
                if (i < allCourseLessons.size() - 1) {
                    dto.setNextLessonId(allCourseLessons.get(i + 1).getId());
                }
                break;
            }
        }

        return dto;
    }

    @Transactional
    public void completeLesson(Long lessonId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));

        boolean alreadyCompleted = lessonProgressRepository.existsByUserIdAndLessonIdAndCompletedTrue(
                student.getId(), lessonId);

        if (!alreadyCompleted) {
            LessonProgress progress = new LessonProgress(student, lesson);
            progress.setCompleted(true);
            lessonProgressRepository.save(progress);
        }

        // Always update topic mastery composite for this topic
        masteryService.updateMasteryFromLessonCompletion(student.getId(), lesson.getTopic().getId());

        // Check if all course lessons are completed and final assessment was already passed
        Long courseId = lesson.getTopic().getCourse().getId();
        int totalLessons = lessonRepository.findByTopicCourseIdOrderByTopicOrderIndexAscOrderIndexAsc(courseId).size();
        int completedCount = lessonProgressRepository.findByUserIdAndLessonTopicCourseIdAndCompletedTrue(student.getId(), courseId).size();

        if (totalLessons > 0 && completedCount >= totalLessons) {
            enrollmentRepository.findByUserIdAndCourseId(student.getId(), courseId).ifPresent(enrollment -> {
                if (enrollment.isFinalAssessmentPassed() && !enrollment.isCompleted()) {
                    enrollment.setCompleted(true);
                    if (enrollment.getCompletedAt() == null) {
                        enrollment.setCompletedAt(LocalDateTime.now());
                    }
                    enrollmentRepository.save(enrollment);
                    certificateService.generateCertificateIfEligible(student, lesson.getTopic().getCourse(), enrollment.getFinalScore());
                }
            });
        }
    }

    @Transactional(readOnly = true)
    public List<Long> getCompletedLessonIds(String studentEmail, Long courseId) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<LessonProgress> list;
        if (courseId != null) {
            list = lessonProgressRepository.findByUserIdAndLessonTopicCourseIdAndCompletedTrue(student.getId(), courseId);
        } else {
            list = lessonProgressRepository.findByUserIdAndCompletedTrue(student.getId());
        }

        return list.stream()
                .map(lp -> lp.getLesson().getId())
                .toList();
    }

    public CourseResponse toCourseResponse(Course course) {
        int topicCount = topicRepository.findByCourseIdOrderByOrderIndexAsc(course.getId()).size();
        long enrollmentCount = enrollmentRepository.countByCourseId(course.getId());
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getThumbnailUrl(),
                course.getInstructor().getName(),
                course.isPublished(),
                topicCount,
                enrollmentCount
        );
    }

    private TopicResponse toTopicResponse(Topic topic) {
        List<LessonResponse> lessons = lessonRepository.findByTopicIdOrderByOrderIndexAsc(topic.getId())
                .stream()
                .map(this::toLessonResponse)
                .toList();
        return new TopicResponse(
                topic.getId(),
                topic.getTitle(),
                topic.getDescription(),
                topic.getOrderIndex(),
                lessons
        );
    }

    private LessonResponse toLessonResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getTopic().getCourse().getId(),
                lesson.getTopic().getId(),
                lesson.getTopic().getTitle(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getKeyPoints(),
                lesson.getEstimatedMinutes(),
                lesson.getOrderIndex()
        );
    }
}
