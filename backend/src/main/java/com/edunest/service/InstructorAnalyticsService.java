package com.edunest.service;

import com.edunest.dto.response.CourseAnalyticsResponse;
import com.edunest.dto.response.TopicAnalyticsDto;
import com.edunest.entity.Course;
import com.edunest.entity.Topic;
import com.edunest.entity.TopicMastery;
import com.edunest.enums.MasteryLevel;
import com.edunest.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InstructorAnalyticsService {

    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TopicMasteryRepository masteryRepository;
    private final UserRepository userRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final SubmissionRepository submissionRepository;

    public InstructorAnalyticsService(CourseRepository courseRepository,
                                      TopicRepository topicRepository,
                                      EnrollmentRepository enrollmentRepository,
                                      TopicMasteryRepository masteryRepository,
                                      UserRepository userRepository,
                                      QuizAttemptRepository quizAttemptRepository,
                                      SubmissionRepository submissionRepository) {
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.masteryRepository = masteryRepository;
        this.userRepository = userRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.submissionRepository = submissionRepository;
    }

    @Transactional(readOnly = true)
    public CourseAnalyticsResponse getCourseAnalytics(Long courseId) {
        Course course;
        if (courseId != null) {
            course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));
        } else {
            course = courseRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No courses available"));
        }

        long enrolledCount = enrollmentRepository.countByCourseId(course.getId());
        List<Topic> topics = topicRepository.findByCourseIdOrderByOrderIndexAsc(course.getId());
        List<TopicMastery> allMasteries = masteryRepository.findByTopicCourseId(course.getId());

        int activeStudents = (int) allMasteries.stream()
                .map(m -> m.getUser().getId())
                .distinct()
                .count();

        Map<Long, List<TopicMastery>> byTopic = allMasteries.stream()
                .collect(Collectors.groupingBy(m -> m.getTopic().getId()));

        List<TopicAnalyticsDto> breakdown = new ArrayList<>();
        double totalScoreSum = 0.0;
        int assessedTopicsCount = 0;

        for (Topic topic : topics) {
            List<TopicMastery> records = byTopic.getOrDefault(topic.getId(), Collections.emptyList());
            int assessed = records.size();
            double avg = 0.0;
            int weakCount = 0;
            int masteredCount = 0;

            if (assessed > 0) {
                double sum = 0.0;
                for (TopicMastery m : records) {
                    sum += m.getMasteryScore();
                    if (m.getMasteryLevel() == MasteryLevel.WEAK || m.getMasteryLevel() == MasteryLevel.NEEDS_WORK) {
                        weakCount++;
                    } else if (m.getMasteryLevel() == MasteryLevel.MASTERED) {
                        masteredCount++;
                    }
                }
                avg = Math.round((sum / assessed) * 10.0) / 10.0;
                totalScoreSum += avg;
                assessedTopicsCount++;
            }

            breakdown.add(new TopicAnalyticsDto(
                    topic.getId(),
                    topic.getTitle(),
                    topic.getOrderIndex(),
                    avg,
                    assessed,
                    weakCount,
                    masteredCount
            ));
        }

        double overallAvg = assessedTopicsCount == 0
                ? 0.0
                : Math.round((totalScoreSum / assessedTopicsCount) * 10.0) / 10.0;

        List<TopicAnalyticsDto> weakestTopics = breakdown.stream()
                .filter(t -> t.getStudentsAssessed() > 0)
                .sorted(Comparator.comparingDouble(TopicAnalyticsDto::getAverageMasteryScore))
                .limit(3)
                .toList();

        return new CourseAnalyticsResponse(
                course.getId(),
                course.getTitle(),
                enrolledCount,
                activeStudents,
                overallAvg,
                weakestTopics,
                breakdown
        );
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAdminPlatformOverview() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalCourses", courseRepository.count());
        stats.put("totalEnrollments", enrollmentRepository.count());
        stats.put("totalQuizAttempts", quizAttemptRepository.count());
        stats.put("totalChallengeSubmissions", submissionRepository.count());
        return stats;
    }
}
