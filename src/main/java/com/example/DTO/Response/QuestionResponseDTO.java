package com.example.DTO.Response;

import com.example.Enum.DifficultyLevel;
import com.example.Enum.MediaType;
import com.example.Enum.QuestionType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class QuestionResponseDTO {
    private UUID id;

    @com.fasterxml.jackson.annotation.JsonProperty("quiz_id")
    private UUID quizId;

    @com.fasterxml.jackson.annotation.JsonProperty("question_text")
    private String questionText;

    private String slug;

    @com.fasterxml.jackson.annotation.JsonProperty("question_type")
    private QuestionType questionType;

    private Double points;

    @com.fasterxml.jackson.annotation.JsonProperty("time_limit")
    private Integer timeLimit;

    private String content;

    private String explanation;

    @com.fasterxml.jackson.annotation.JsonProperty("media_id")
    private UUID mediaId;

    @com.fasterxml.jackson.annotation.JsonProperty("media_type")
    private MediaType mediaType;

    @com.fasterxml.jackson.annotation.JsonProperty("difficulty_level")
    private DifficultyLevel difficultyLevel;

    @com.fasterxml.jackson.annotation.JsonProperty("sort_order")
    private Integer sortOrder;

    @com.fasterxml.jackson.annotation.JsonProperty("is_required")
    private Boolean isRequired;

    private String settings;

    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private LocalDateTime createdAt;

    @com.fasterxml.jackson.annotation.JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @com.fasterxml.jackson.annotation.JsonProperty("quiz_title")
    private String quizTitle;

    @com.fasterxml.jackson.annotation.JsonProperty("options_count")
    private Integer optionsCount;

    private java.util.List<QuestionOptionResponseDTO> options;
}
