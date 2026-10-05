package com.edunest.service;

import com.edunest.dto.request.ChallengeSubmissionRequest;
import com.edunest.dto.request.CreateChallengeRequest;
import com.edunest.dto.response.ChallengeDto;
import com.edunest.dto.response.SubmissionResponse;
import com.edunest.entity.*;
import com.edunest.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final TopicRepository topicRepository;
    private final TopicMasteryRepository masteryRepository;
    private final UserRepository userRepository;

    public ChallengeService(ChallengeRepository challengeRepository,
                            SubmissionRepository submissionRepository,
                            TopicRepository topicRepository,
                            TopicMasteryRepository masteryRepository,
                            UserRepository userRepository) {
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
        this.topicRepository = topicRepository;
        this.masteryRepository = masteryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ChallengeDto> getChallengesForCourse(String studentEmail, Long courseId) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Challenge> challenges = challengeRepository.findByTopicCourseIdOrderByTopicOrderIndexAsc(courseId);
        return challenges.stream()
                .map(c -> toDto(c, student.getId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChallengeDto> getUnlockedChallengesForCourse(String studentEmail, Long courseId) {
        return getChallengesForCourse(studentEmail, courseId).stream()
                .filter(ChallengeDto::isUnlocked)
                .toList();
    }

    @Transactional(readOnly = true)
    public ChallengeDto getChallengeById(Long challengeId, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
        return toDto(challenge, student.getId());
    }

    @Transactional
    public SubmissionResponse submitChallenge(Long challengeId, ChallengeSubmissionRequest req, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        Topic topic = challenge.getTopic();
        double currentMastery = masteryRepository.findByUserIdAndTopicId(student.getId(), topic.getId())
                .map(TopicMastery::getMasteryScore)
                .orElse(0.0);

        if (currentMastery < challenge.getUnlockThreshold()) {
            throw new RuntimeException("Challenge is locked! Reach at least " + challenge.getUnlockThreshold()
                    + "% mastery in '" + topic.getTitle() + "' (Current: " + currentMastery + "%) to unlock.");
        }

        String solution = req.getSolutionText() != null ? req.getSolutionText().trim() : "";
        String keywordsRaw = challenge.getExpectedKeywords();

        double score;
        boolean passed;
        String feedback;

        if (keywordsRaw != null && !keywordsRaw.isBlank()) {
            String[] tokens = keywordsRaw.split(";");
            List<String> matched = new ArrayList<>();
            List<String> missing = new ArrayList<>();

            for (String token : tokens) {
                String clean = token.trim();
                if (!clean.isEmpty()) {
                    if (solution.contains(clean)) {
                        matched.add(clean);
                    } else {
                        missing.add(clean);
                    }
                }
            }

            int total = matched.size() + missing.size();
            score = total == 0 ? 100.0 : Math.round((matched.size() * 1000.0) / total) / 10.0;
            passed = score >= 75.0;

            if (passed) {
                feedback = "Excellent work! Your solution passed rule-based verification (" + score + "%). Verified constructs: "
                        + String.join(", ", matched) + ".";
            } else {
                feedback = "Partial solution (" + score + "%). Make sure your implementation includes required Java constructs: "
                        + String.join(", ", missing) + ".";
            }
        } else {
            passed = solution.length() >= 15;
            score = passed ? 100.0 : 40.0;
            feedback = passed
                    ? "Solution submitted and verified successfully!"
                    : "Your solution is too brief. Please provide a complete code or concept implementation.";
        }

        Submission submission = new Submission();
        submission.setChallenge(challenge);
        submission.setUser(student);
        submission.setSolutionText(solution);
        submission.setPassed(passed);
        submission.setScore(score);
        submission.setFeedback(feedback);

        Submission saved = submissionRepository.save(submission);
        return new SubmissionResponse(
                saved.getId(),
                challenge.getId(),
                challenge.getTitle(),
                saved.isPassed(),
                saved.getScore(),
                saved.getFeedback(),
                saved.getSubmittedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<SubmissionResponse> getMySubmissions(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        return submissionRepository.findByUserIdOrderBySubmittedAtDesc(student.getId()).stream()
                .map(s -> new SubmissionResponse(
                        s.getId(),
                        s.getChallenge().getId(),
                        s.getChallenge().getTitle(),
                        s.isPassed(),
                        s.getScore(),
                        s.getFeedback(),
                        s.getSubmittedAt()
                ))
                .toList();
    }

    @Transactional
    public ChallengeDto createChallenge(CreateChallengeRequest req) {
        Topic topic = topicRepository.findById(req.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));

        Challenge c = new Challenge();
        c.setTopic(topic);
        c.setTitle(req.getTitle());
        c.setDescription(req.getDescription());
        c.setStarterCode(req.getStarterCode());
        c.setExpectedKeywords(req.getExpectedKeywords());
        c.setChallengeType(req.getChallengeType());
        c.setDifficulty(req.getDifficulty());
        c.setPoints(req.getPoints());
        c.setUnlockThreshold(req.getUnlockThreshold());

        Challenge saved = challengeRepository.save(c);
        return new ChallengeDto(
                saved.getId(),
                topic.getId(),
                topic.getTitle(),
                topic.getCourse().getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getStarterCode(),
                saved.getChallengeType(),
                saved.getDifficulty(),
                saved.getPoints(),
                saved.getUnlockThreshold(),
                0.0,
                false,
                false,
                null,
                null
        );
    }

    private ChallengeDto toDto(Challenge c, Long userId) {
        Topic topic = c.getTopic();
        double currentMastery = masteryRepository.findByUserIdAndTopicId(userId, topic.getId())
                .map(TopicMastery::getMasteryScore)
                .orElse(0.0);

        boolean unlocked = currentMastery >= c.getUnlockThreshold();
        List<Submission> subs = submissionRepository.findByUserIdAndChallengeIdOrderBySubmittedAtDesc(userId, c.getId());

        boolean completed = subs.stream().anyMatch(Submission::isPassed);
        Double bestScore = subs.stream()
                .map(Submission::getScore)
                .max(Double::compareTo)
                .orElse(null);
        String lastFeedback = subs.isEmpty() ? null : subs.get(0).getFeedback();

        return new ChallengeDto(
                c.getId(),
                topic.getId(),
                topic.getTitle(),
                topic.getCourse().getId(),
                c.getTitle(),
                c.getDescription(),
                c.getStarterCode(),
                c.getChallengeType(),
                c.getDifficulty(),
                c.getPoints(),
                c.getUnlockThreshold(),
                currentMastery,
                unlocked,
                completed,
                bestScore,
                lastFeedback
        );
    }
}
