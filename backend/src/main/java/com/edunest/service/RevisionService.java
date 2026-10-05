package com.edunest.service;

import com.edunest.dto.response.RevisionDto;
import com.edunest.dto.response.TopicMasteryDto;
import com.edunest.entity.*;
import com.edunest.enums.MasteryLevel;
import com.edunest.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RevisionService {

    private final RevisionScheduleRepository revisionRepository;
    private final TopicMasteryRepository masteryRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final MasteryService masteryService;

    public RevisionService(RevisionScheduleRepository revisionRepository,
                           TopicMasteryRepository masteryRepository,
                           UserRepository userRepository,
                           TopicRepository topicRepository,
                           MasteryService masteryService) {
        this.revisionRepository = revisionRepository;
        this.masteryRepository = masteryRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.masteryService = masteryService;
    }

    /**
     * Completes a scheduled revision task, updates the 20% Revision component in TopicMastery,
     * and schedules the next spaced repetition interval (1d -> 3d -> 7d) if still not MASTERED.
     */
    @Transactional
    public TopicMasteryDto completeRevision(Long revisionId, double reviewScore, String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        RevisionSchedule rev = revisionRepository.findById(revisionId)
                .orElseThrow(() -> new RuntimeException("Revision task not found"));

        if (!rev.getUser().getId().equals(student.getId())) {
            throw new RuntimeException("Not authorized to complete this revision task");
        }

        rev.setCompleted(true);
        rev.setCompletedAt(LocalDateTime.now());
        revisionRepository.save(rev);

        Topic topic = rev.getTopic();
        TopicMastery updated = masteryService.updateMasteryFromAssessment(
                student.getId(), topic.getId(), reviewScore, "REVISION"
        );

        // Spaced Repetition Progression: Stage 1 (1d) -> Stage 2 (+3d) -> Stage 3 (+7d)
        if (rev.getIntervalStage() < 3 && updated.getMasteryLevel() != MasteryLevel.MASTERED) {
            int nextStage = rev.getIntervalStage() + 1;
            int daysToAdd = (nextStage == 2) ? 3 : 7;

            Optional<RevisionSchedule> existingPending =
                    revisionRepository.findFirstByUserIdAndTopicIdAndCompletedFalse(student.getId(), topic.getId());

            if (existingPending.isEmpty()) {
                RevisionSchedule nextRev = new RevisionSchedule();
                nextRev.setUser(student);
                nextRev.setTopic(topic);
                nextRev.setIntervalStage(nextStage);
                nextRev.setDueDate(LocalDate.now().plusDays(daysToAdd));
                nextRev.setCompleted(false);
                revisionRepository.save(nextRev);
            } else {
                RevisionSchedule pending = existingPending.get();
                pending.setIntervalStage(nextStage);
                pending.setDueDate(LocalDate.now().plusDays(daysToAdd));
                revisionRepository.save(pending);
            }
        }

        return new TopicMasteryDto(
                topic.getId(),
                topic.getTitle(),
                topic.getOrderIndex(),
                updated.getDiagnosticScore(),
                updated.getPracticeScore(),
                updated.getQuizScore(),
                updated.getRevisionScore(),
                updated.getMasteryScore(),
                updated.getMasteryLevel(),
                null
        );
    }

    @Transactional(readOnly = true)
    public List<RevisionDto> getStudentRevisions(String studentEmail, boolean onlyPending) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<RevisionSchedule> list = onlyPending
                ? revisionRepository.findByUserIdAndCompletedFalseOrderByDueDateAsc(student.getId())
                : revisionRepository.findByUserIdOrderByCompletedAscDueDateAsc(student.getId());

        LocalDate today = LocalDate.now();

        return list.stream().map(r -> {
            Topic t = r.getTopic();
            double masteryScore = masteryRepository.findByUserIdAndTopicId(student.getId(), t.getId())
                    .map(TopicMastery::getMasteryScore)
                    .orElse(0.0);
            boolean dueOrOverdue = !r.isCompleted() && !r.getDueDate().isAfter(today);

            return new RevisionDto(
                    r.getId(),
                    t.getId(),
                    t.getTitle(),
                    t.getCourse().getId(),
                    r.getDueDate(),
                    r.getIntervalStage(),
                    r.isCompleted(),
                    r.getCompletedAt(),
                    dueOrOverdue,
                    masteryScore
            );
        }).toList();
    }
}
