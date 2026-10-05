package com.edunest.service;

import com.edunest.dto.response.TopicScoreDto;
import com.edunest.entity.QuizAnswerRecord;
import com.edunest.entity.Topic;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScoringService {

    public List<TopicScoreDto> computeTopicBreakdown(List<QuizAnswerRecord> records) {
        Map<Long, List<QuizAnswerRecord>> groupedByTopic = records.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getQuestion().getTopic().getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<TopicScoreDto> breakdown = new ArrayList<>();

        groupedByTopic.forEach((topicId, topicRecords) -> {
            Topic topic = topicRecords.get(0).getQuestion().getTopic();
            int total = topicRecords.size();
            int correct = (int) topicRecords.stream().filter(QuizAnswerRecord::isCorrect).count();
            double pct = Math.round(((double) correct / total) * 1000.0) / 10.0;
            String label = classifyStatus(pct);

            breakdown.add(new TopicScoreDto(
                    topic.getId(),
                    topic.getTitle(),
                    correct,
                    total,
                    pct,
                    label
            ));
        });

        return breakdown;
    }

    public String classifyStatus(double percentage) {
        if (percentage < 40.0) return "WEAK";
        if (percentage < 70.0) return "NEEDS_WORK";
        if (percentage < 90.0) return "STRONG";
        return "MASTERED";
    }
}
