package com.edunest.service;

import com.edunest.dto.request.ChallengeSubmissionRequest;
import com.edunest.dto.request.CreateChallengeRequest;
import com.edunest.dto.response.ChallengeDto;
import com.edunest.dto.response.ChallengeExecutionResponse;
import com.edunest.dto.response.SubmissionResponse;
import com.edunest.entity.*;
import com.edunest.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final TopicRepository topicRepository;
    private final TopicMasteryRepository masteryRepository;
    private final UserRepository userRepository;
    private final CodeExecutionService codeExecutionService;
    private final ObjectMapper objectMapper;

    public ChallengeService(ChallengeRepository challengeRepository,
                            SubmissionRepository submissionRepository,
                            TopicRepository topicRepository,
                            TopicMasteryRepository masteryRepository,
                            UserRepository userRepository,
                            CodeExecutionService codeExecutionService,
                            ObjectMapper objectMapper) {
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
        this.topicRepository = topicRepository;
        this.masteryRepository = masteryRepository;
        this.userRepository = userRepository;
        this.codeExecutionService = codeExecutionService;
        this.objectMapper = objectMapper;
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
    public ChallengeExecutionResponse runChallenge(Long challengeId, ChallengeSubmissionRequest req, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        List<CodeExecutionService.TestCase> sampleCases = parseTestCases(challenge.getSampleTestCasesJson());
        CodeExecutionService.ExecutionResult exec = codeExecutionService.execute(
                req.getLanguage(), req.getSolutionText(), sampleCases
        );

        ChallengeExecutionResponse resp = new ChallengeExecutionResponse();
        resp.setChallengeId(challenge.getId());
        resp.setChallengeTitle(challenge.getTitle());
        resp.setLanguage(req.getLanguage());
        resp.setSubmission(false);
        resp.setCompiled(exec.isCompiled());
        resp.setCompilationOutput(exec.getCompilationOutput());
        resp.setPassed(exec.isPassed());
        resp.setScore(exec.getScore());
        resp.setTotalTestCases(exec.getTotalTestCases());
        resp.setPassedTestCases(exec.getPassedTestCases());
        resp.setTestCaseResults(exec.getTestCaseResults());
        resp.setFeedback(exec.getFeedback());
        resp.setExecutionTimeMs(exec.getTotalExecutionTimeMs());
        return resp;
    }

    @Transactional
    public ChallengeExecutionResponse submitChallenge(Long challengeId, ChallengeSubmissionRequest req, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        Topic topic = challenge.getTopic();
        double currentMastery = masteryRepository.findByUserIdAndTopicId(student.getId(), topic.getId())
                .map(TopicMastery::getMasteryScore)
                .orElse(0.0);

        if (challenge.getUnlockThreshold() > 0.0 && currentMastery < challenge.getUnlockThreshold()) {
            throw new RuntimeException("Challenge is locked! Reach at least " + challenge.getUnlockThreshold()
                    + "% mastery in '" + topic.getTitle() + "' (Current: " + currentMastery + "%) to unlock.");
        }

        List<CodeExecutionService.TestCase> allCases = new ArrayList<>();
        allCases.addAll(parseTestCases(challenge.getSampleTestCasesJson()));
        allCases.addAll(parseTestCases(challenge.getHiddenTestCasesJson()));

        CodeExecutionService.ExecutionResult exec;
        if (!allCases.isEmpty()) {
            exec = codeExecutionService.execute(req.getLanguage(), req.getSolutionText(), allCases);
        } else {
            exec = fallbackLegacyVerification(challenge, req.getSolutionText());
        }

        // A challenge is marked completed ONLY if the submitted code compiles successfully and passes all test cases
        boolean passed = exec.isCompiled() && exec.isPassed();
        double score = passed ? 100.0 : (exec.isCompiled() ? exec.getScore() : 0.0);

        Submission submission = new Submission();
        submission.setChallenge(challenge);
        submission.setUser(student);
        submission.setSolutionText(req.getSolutionText() != null ? req.getSolutionText() : "");
        submission.setPassed(passed);
        submission.setScore(score);
        submission.setFeedback(exec.getFeedback());

        Submission saved = submissionRepository.save(submission);

        ChallengeExecutionResponse resp = new ChallengeExecutionResponse();
        resp.setSubmissionId(saved.getId());
        resp.setChallengeId(challenge.getId());
        resp.setChallengeTitle(challenge.getTitle());
        resp.setLanguage(req.getLanguage());
        resp.setSubmission(true);
        resp.setCompiled(exec.isCompiled());
        resp.setCompilationOutput(exec.getCompilationOutput());
        resp.setPassed(passed);
        resp.setScore(score);
        resp.setTotalTestCases(exec.getTotalTestCases());
        resp.setPassedTestCases(exec.getPassedTestCases());
        resp.setTestCaseResults(exec.getTestCaseResults());
        resp.setFeedback(exec.getFeedback());
        resp.setExecutionTimeMs(exec.getTotalExecutionTimeMs());
        return resp;
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
        return toDto(saved, 0L);
    }

    private ChallengeDto toDto(Challenge c, Long userId) {
        Topic topic = c.getTopic();
        double currentMastery = userId != null ? masteryRepository.findByUserIdAndTopicId(userId, topic.getId())
                .map(TopicMastery::getMasteryScore)
                .orElse(0.0) : 0.0;

        boolean unlocked = currentMastery >= c.getUnlockThreshold() || c.getUnlockThreshold() <= 0.0;
        List<Submission> subs = userId != null
                ? submissionRepository.findByUserIdAndChallengeIdOrderBySubmittedAtDesc(userId, c.getId())
                : Collections.emptyList();

        boolean completed = subs.stream().anyMatch(Submission::isPassed);
        Double bestScore = subs.stream()
                .map(Submission::getScore)
                .max(Double::compareTo)
                .orElse(null);
        String lastFeedback = subs.isEmpty() ? null : subs.get(0).getFeedback();

        ChallengeDto dto = new ChallengeDto(
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
        dto.setStarterCodePython(c.getStarterCodePython());
        dto.setStarterCodeJs(c.getStarterCodeJs());
        dto.setInputFormat(c.getInputFormat());
        dto.setOutputFormat(c.getOutputFormat());
        dto.setConstraints(c.getConstraints());
        dto.setSampleTestCasesJson(c.getSampleTestCasesJson());
        return dto;
    }

    private List<CodeExecutionService.TestCase> parseTestCases(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<CodeExecutionService.TestCase>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private CodeExecutionService.ExecutionResult fallbackLegacyVerification(Challenge challenge, String solutionText) {
        CodeExecutionService.ExecutionResult res = new CodeExecutionService.ExecutionResult();
        String solution = solutionText != null ? solutionText.trim() : "";
        String keywordsRaw = challenge.getExpectedKeywords();

        if (keywordsRaw != null && !keywordsRaw.isBlank()) {
            String[] tokens = keywordsRaw.split(";");
            int matched = 0;
            int total = 0;
            for (String t : tokens) {
                String clean = t.trim();
                if (!clean.isEmpty()) {
                    total++;
                    if (solution.contains(clean)) matched++;
                }
            }
            double score = total == 0 ? 100.0 : Math.round((matched * 1000.0) / total) / 10.0;
            boolean passed = score >= 75.0 && solution.length() >= 20;
            res.setCompiled(true);
            res.setPassed(passed);
            res.setScore(score);
            res.setFeedback(passed ? "Verified constructs successfully (" + score + "%)." : "Missing required syntax constructs.");
        } else {
            boolean passed = solution.length() >= 20;
            res.setCompiled(true);
            res.setPassed(passed);
            res.setScore(passed ? 100.0 : 40.0);
            res.setFeedback(passed ? "Solution verified successfully!" : "Solution too brief.");
        }
        return res;
    }
}
