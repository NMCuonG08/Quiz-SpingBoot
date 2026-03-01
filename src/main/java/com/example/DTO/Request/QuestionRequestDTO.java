package com.example.DTO.Request;

import com.example.Enum.DifficultyLevel;
import com.example.Enum.MediaType;
import com.example.Enum.QuestionType;
import lombok.Data;

import java.util.UUID;

@Data
public class QuestionRequestDTO {
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
    private org.springframework.web.multipart.MultipartFile file;
}
