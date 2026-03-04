package com.example.DTO.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class QuizSessionPublicResponseDTO {
    private UUID id;

    @JsonProperty("quiz_id")
    private UUID quizId;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("started_at")
    private LocalDateTime startedAt;

    @JsonProperty("current_question_index")
    private Integer currentQuestionIndex;

    @JsonProperty("total_questions")
    private Integer totalQuestions;

    private Double score;

    @JsonProperty("time_spent")
    private Integer timeSpent;

    private List<Object> answers;

    @JsonProperty("is_resume")
    private Boolean isResume;
}
