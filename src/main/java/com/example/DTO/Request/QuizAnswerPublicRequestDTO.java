package com.example.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

@Data
public class QuizAnswerPublicRequestDTO {
    @JsonProperty("question_id")
    private UUID questionId;

    @JsonProperty("selected_option_id")
    private String selectedOptionId;

    @JsonProperty("selected_option_ids")
    private List<String> selectedOptionIds;

    @JsonProperty("text_answer")
    private String textAnswer;

    @JsonProperty("time_spent")
    private Integer timeSpent;

    @JsonProperty("is_correct")
    private Boolean isCorrect;

    @JsonProperty("points_earned")
    private Double pointsEarned;

    @JsonProperty("answered_at")
    private LocalDateTime answeredAt;
}
