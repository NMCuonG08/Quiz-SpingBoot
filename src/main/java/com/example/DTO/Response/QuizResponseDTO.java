package com.example.DTO.Response;

import com.example.Enum.DifficultyLevel;
import com.example.Enum.QuizType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class QuizResponseDTO {
    private UUID id;
    private String title;
    private String slug;
    private String description;

    @JsonProperty("category_id")
    private UUID categoryId;

    @JsonProperty("category_name")
    private String categoryName;

    @JsonProperty("creator_id")
    private UUID creatorId;

    @JsonProperty("creator_name")
    private String creatorName;

    @JsonProperty("difficulty_level")
    private DifficultyLevel difficultyLevel;

    @JsonProperty("time_limit")
    private Integer timeLimit;

    @JsonProperty("max_attempts")
    private Integer maxAttempts;

    @JsonProperty("passing_score")
    private Double passingScore;

    @JsonProperty("is_public")
    private Boolean isPublic;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("quiz_type")
    private QuizType quizType;

    private String instructions;

    @JsonProperty("thumbnail_id")
    private UUID thumbnailId;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;

    @JsonProperty("questions_count")
    private Integer questionsCount;

    @JsonProperty("attempts_count")
    private Integer attemptsCount;

    private List<String> tags;
    private String settings;

    @JsonProperty("average_rating")
    private Double averageRating;

    @JsonProperty("total_ratings")
    private Integer totalRatings;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
