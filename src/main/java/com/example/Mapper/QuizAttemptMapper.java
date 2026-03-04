package com.example.Mapper;

import com.example.DTO.Response.QuizAttemptResponseDTO;
import com.example.Entity.QuizAttempt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuizAttemptMapper {

    public QuizAttemptResponseDTO toResponseDTO(QuizAttempt entity) {
        if (entity == null)
            return null;

        return QuizAttemptResponseDTO.builder()
                .id(entity.getId())
                .quizId(entity.getQuiz_id())
                .userId(entity.getUser_id())
                .sessionId(entity.getSession_id())
                .attemptNumber(entity.getAttempt_number())
                .status(entity.getStatus())
                .score(entity.getScore())
                .maxScore(entity.getMax_score())
                .percentage(entity.getPercentage())
                .passed(entity.getPassed())
                .timeTaken(entity.getTime_taken())
                .startedAt(entity.getStarted_at())
                .completedAt(entity.getCompleted_at())
                .quizTitle(entity.getQuiz() != null ? entity.getQuiz().getTitle() : null)
                .thumbnailUrl(entity.getQuiz() != null && entity.getQuiz().getThumbnail() != null
                        ? entity.getQuiz().getThumbnail().getUrl()
                        : null)
                .build();
    }

    public List<QuizAttemptResponseDTO> toResponseDTOList(List<QuizAttempt> entities) {
        if (entities == null)
            return null;
        return entities.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
