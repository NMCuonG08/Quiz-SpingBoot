package com.example.DTO.Response;

import com.example.Enum.AttemptStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class QuizAttemptResponseDTO {
    private UUID id;

    @JsonProperty("quiz_id")
    private UUID quizId;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("session_id")
    private UUID sessionId;

    @JsonProperty("attempt_number")
    private Integer attemptNumber;

    private AttemptStatus status;

    private Double score;

    @JsonProperty("max_score")
    private Double maxScore;

    private Double percentage;

    private Boolean passed;

    @JsonProperty("time_taken")
    private Integer timeTaken;

    @JsonProperty("started_at")
    private LocalDateTime startedAt;

    @JsonProperty("completed_at")
    private LocalDateTime completedAt;

    @JsonProperty("quiz_title")
    private String quizTitle;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
}
