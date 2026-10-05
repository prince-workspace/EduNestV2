package com.edunest.service;

import com.edunest.dto.request.CreateQuestionRequest;
import com.edunest.dto.request.QuizSubmissionRequest;
import com.edunest.dto.response.*;
import com.edunest.entity.*;
import com.edunest.enums.QuestionType;
import com.edunest.enums.QuizType;
import com.edunest.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuizAnswerRecordRepository answerRecordRepository;
    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final ScoringService scoringService;
    private final MasteryService masteryService;

    public QuizService(QuizRepository quizRepository,
                       QuestionRepository questionRepository,
                       QuizAttemptRepository attemptRepository,
                       QuizAnswerRecordRepository answerRecordRepository,
                       CourseRepository courseRepository,
                       TopicRepository topicRepository,
                       EnrollmentRepository enrollmentRepository,
                       UserRepository userRepository,
                       ScoringService scoringService,
                       MasteryService masteryService) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.scoringService = scoringService;
        this.masteryService = masteryService;
    }

    @Transactional
    public QuestionDto addQuestionToTopic(Long topicId, CreateQuestionRequest req, String instructorEmail) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        if (!topic.getCourse().getInstructor().getEmail().equalsIgnoreCase(instructorEmail)) {
            throw new RuntimeException("Not authorized to add questions to this topic");
        }

        Question q = new Question();
        q.setTopic(topic);
        q.setQuestionText(req.getQuestionText().trim());
        q.setOptionA(req.getOptionA().trim());
        q.setOptionB(req.getOptionB().trim());
        q.setOptionC(req.getOptionC() != null ? req.getOptionC().trim() : null);
        q.setOptionD(req.getOptionD() != null ? req.getOptionD().trim() : null);
        q.setCorrectOption(req.getCorrectOption().trim().toUpperCase());
        q.setQuestionType(req.getQuestionType());
        q.setExplanation(req.getExplanation());

        Question saved = questionRepository.save(q);
        return toQuestionDto(saved, true);
    }

    @Transactional
    public QuizQuestionsResponse getDiagnosticQuizQuestions(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Quiz quiz = quizRepository.findByCourseIdAndQuizType(courseId, QuizType.DIAGNOSTIC)
                .orElseGet(() -> {
                    List<Question> diagQuestions =
                            questionRepository.findByTopicCourseIdAndQuestionType(courseId, QuestionType.DIAGNOSTIC);
                    if (diagQuestions.isEmpty()) {
                        diagQuestions = questionRepository.findAll().stream()
                                .filter(q -> q.getTopic().getCourse().getId().equals(courseId))
                                .toList();
                    }
                    Quiz newQuiz = new Quiz();
                    newQuiz.setCourse(course);
                    newQuiz.setTitle(course.getTitle() + " - Diagnostic Assessment");
                    newQuiz.setQuizType(QuizType.DIAGNOSTIC);
                    newQuiz.setTimeLimitMinutes(15);
                    newQuiz.setQuestions(new ArrayList<>(diagQuestions));
                    return quizRepository.save(newQuiz);
                });

        List<QuestionDto> questions = quiz.getQuestions().stream()
                .map(q -> toQuestionDto(q, false))
                .toList();

        return new QuizQuestionsResponse(
                quiz.getId(),
                course.getId(),
                null,
                quiz.getTitle(),
                quiz.getQuizType(),
                quiz.getTimeLimitMinutes(),
                questions
        );
    }

    @Transactional
    public QuizQuestionsResponse getPracticeQuestions(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        Quiz quiz = quizRepository.findByTopicIdAndQuizType(topicId, QuizType.PRACTICE)
                .orElseGet(() -> {
                    List<Question> practiceQuestions =
                            questionRepository.findByTopicIdAndQuestionType(topicId, QuestionType.PRACTICE);
                    if (practiceQuestions.isEmpty()) {
                        practiceQuestions = questionRepository.findByTopicId(topicId);
                    }
                    Quiz newQuiz = new Quiz();
                    newQuiz.setCourse(topic.getCourse());
                    newQuiz.setTopic(topic);
                    newQuiz.setTitle(topic.getTitle() + " - Concept Practice");
                    newQuiz.setQuizType(QuizType.PRACTICE);
                    newQuiz.setTimeLimitMinutes(null);
                    newQuiz.setQuestions(new ArrayList<>(practiceQuestions));
                    return quizRepository.save(newQuiz);
                });

        // Practice mode includes correctOption + explanation so student gets instant feedback
        List<QuestionDto> questions = quiz.getQuestions().stream()
                .map(q -> toQuestionDto(q, true))
                .toList();

        return new QuizQuestionsResponse(
                quiz.getId(),
                topic.getCourse().getId(),
                topic.getId(),
                quiz.getTitle(),
                quiz.getQuizType(),
                null,
                questions
        );
    }

    @Transactional
    public QuizQuestionsResponse getChapterQuizQuestions(Long topicId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        Quiz quiz = quizRepository.findByTopicIdAndQuizType(topicId, QuizType.CHAPTER_QUIZ)
                .orElseGet(() -> {
                    List<Question> quizQuestions =
                            questionRepository.findByTopicIdAndQuestionType(topicId, QuestionType.QUIZ);
                    if (quizQuestions.isEmpty()) {
                        quizQuestions = questionRepository.findByTopicId(topicId);
                    }
                    Quiz newQuiz = new Quiz();
                    newQuiz.setCourse(topic.getCourse());
                    newQuiz.setTopic(topic);
                    newQuiz.setTitle(topic.getTitle() + " - Topic Assessment Quiz");
                    newQuiz.setQuizType(QuizType.CHAPTER_QUIZ);
                    newQuiz.setTimeLimitMinutes(10);
                    newQuiz.setQuestions(new ArrayList<>(quizQuestions));
                    return quizRepository.save(newQuiz);
                });

        List<QuestionDto> questions = quiz.getQuestions().stream()
                .map(q -> toQuestionDto(q, false))
                .toList();

        return new QuizQuestionsResponse(
                quiz.getId(),
                topic.getCourse().getId(),
                topic.getId(),
                quiz.getTitle(),
                quiz.getQuizType(),
                quiz.getTimeLimitMinutes(),
                questions
        );
    }

    @Transactional
    public QuizResultResponse submitAttempt(QuizSubmissionRequest req, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Quiz quiz = quizRepository.findById(req.getQuizId())
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        Map<Long, String> submittedAnswers = new HashMap<>();
        if (req.getAnswers() != null) {
            for (QuizSubmissionRequest.QuestionAnswer qa : req.getAnswers()) {
                if (qa.getQuestionId() != null && qa.getSelectedOption() != null) {
                    submittedAnswers.put(qa.getQuestionId(), qa.getSelectedOption().trim().toUpperCase());
                }
            }
        }

        int correctCount = 0;
        List<QuizAnswerRecord> records = new ArrayList<>();

        for (Question q : quiz.getQuestions()) {
            String selected = submittedAnswers.get(q.getId());
            boolean isCorrect = selected != null && selected.equalsIgnoreCase(q.getCorrectOption());
            if (isCorrect) {
                correctCount++;
            }

            QuizAnswerRecord record = new QuizAnswerRecord();
            record.setQuestion(q);
            record.setSelectedOption(selected);
            record.setCorrect(isCorrect);
            records.add(record);
        }

        int total = quiz.getQuestions().size();
        double percentage = total > 0
                ? Math.round(((double) correctCount / total) * 1000.0) / 10.0
                : 0.0;

        QuizAttempt attempt = new QuizAttempt();
        attempt.setUser(student);
        attempt.setQuiz(quiz);
        attempt.setScore(correctCount);
        attempt.setTotalQuestions(total);
        attempt.setPercentage(percentage);
        attempt = attemptRepository.save(attempt);

        for (QuizAnswerRecord r : records) {
            r.setAttempt(attempt);
        }
        answerRecordRepository.saveAll(records);

        // Update per-topic mastery using the assessment component weight
        List<TopicScoreDto> topicBreakdown = scoringService.computeTopicBreakdown(records);
        String component = quiz.getQuizType().name();
        for (TopicScoreDto ts : topicBreakdown) {
            masteryService.updateMasteryFromAssessment(
                    student.getId(), ts.getTopicId(), ts.getPercentage(), component);
        }

        // If DIAGNOSTIC quiz, mark enrollment.diagnosticCompleted = true
        if (quiz.getQuizType() == QuizType.DIAGNOSTIC) {
            Long courseId = quiz.getCourse().getId();
            Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(student.getId(), courseId)
                    .orElseGet(() -> {
                        Enrollment e = new Enrollment();
                        e.setUser(student);
                        e.setCourse(quiz.getCourse());
                        return e;
                    });
            enrollment.setDiagnosticCompleted(true);
            enrollmentRepository.save(enrollment);
        }

        return buildResultResponse(attempt, records, quiz);
    }

    @Transactional(readOnly = true)
    public QuizResultResponse getAttemptResult(Long attemptId, String studentEmail) {
        QuizAttempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Quiz attempt not found"));

        List<QuizAnswerRecord> records = answerRecordRepository.findByAttemptId(attemptId);
        return buildResultResponse(attempt, records, attempt.getQuiz());
    }

    @Transactional(readOnly = true)
    public List<AttemptSummaryResponse> getMyAttempts(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return attemptRepository.findByUserIdOrderByAttemptedAtDesc(student.getId()).stream()
                .map(a -> new AttemptSummaryResponse(
                        a.getId(),
                        a.getQuiz().getId(),
                        a.getQuiz().getTitle(),
                        a.getQuiz().getQuizType(),
                        a.getScore(),
                        a.getTotalQuestions(),
                        a.getPercentage(),
                        a.getAttemptedAt()
                ))
                .toList();
    }

    private QuizResultResponse buildResultResponse(QuizAttempt attempt,
                                                   List<QuizAnswerRecord> records,
                                                   Quiz quiz) {
        List<TopicScoreDto> topicBreakdown = scoringService.computeTopicBreakdown(records);

        List<QuizResultResponse.AnswerDetail> details = records.stream()
                .map(r -> {
                    Question q = r.getQuestion();
                    return new QuizResultResponse.AnswerDetail(
                            q.getId(),
                            q.getTopic().getId(),
                            q.getTopic().getTitle(),
                            q.getQuestionText(),
                            r.getSelectedOption(),
                            q.getCorrectOption(),
                            r.isCorrect(),
                            q.getExplanation()
                    );
                })
                .toList();

        return new QuizResultResponse(
                attempt.getId(),
                quiz.getCourse().getId(),
                quiz.getTitle(),
                quiz.getQuizType(),
                attempt.getScore(),
                attempt.getTotalQuestions(),
                attempt.getPercentage(),
                topicBreakdown,
                details
        );
    }

    private QuestionDto toQuestionDto(Question q, boolean includeAnswer) {
        return new QuestionDto(
                q.getId(),
                q.getTopic().getId(),
                q.getTopic().getTitle(),
                q.getQuestionText(),
                q.getOptionA(),
                q.getOptionB(),
                q.getOptionC(),
                q.getOptionD(),
                includeAnswer ? q.getCorrectOption() : null,
                includeAnswer ? q.getExplanation() : null
        );
    }
}
