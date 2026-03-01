package com.example.DTO.Response;

import com.example.Enum.DifficultyLevel;
import com.example.Enum.QuizType;
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
    private UUID categoryId;
    private UUID creatorId;
    private DifficultyLevel difficultyLevel;
    private Integer timeLimit;
    private Integer maxAttempts;
    private Double passingScore;
    private Boolean isPublic;
    private Boolean isActive;
    private QuizType quizType;
    private String instructions;
    private UUID thumbnailId;
    private List<String> tags;
    private String settings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
