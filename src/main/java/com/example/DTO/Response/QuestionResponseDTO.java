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
    private UUID quizId;
    private String questionText;
    private String slug;
    private QuestionType questionType;
    private Double points;
    private Integer timeLimit;
    private String explanation;
    private UUID mediaId;
    private MediaType mediaType;
    private DifficultyLevel difficultyLevel;
    private Integer sortOrder;
    private Boolean isRequired;
    private String settings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
