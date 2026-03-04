package com.example.DTO.Request;

import com.example.Enum.DifficultyLevel;
import com.example.Enum.MediaType;
import com.example.Enum.QuestionType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class QuestionRequestDTO {
    @JsonProperty("quiz_id")
    private UUID quizId;

    @JsonProperty("question_text")
    private String questionText;

    private String slug;

    @JsonProperty("question_type")
    private QuestionType questionType;

    private Double points;

    @JsonProperty("time_limit")
    private Integer timeLimit;

    private String explanation;

    @JsonProperty("media_id")
    private UUID mediaId;

    @JsonProperty("media_type")
    private MediaType mediaType;

    @JsonProperty("difficulty_level")
    private DifficultyLevel difficultyLevel;

    @JsonProperty("sort_order")
    private Integer sortOrder;

    @JsonProperty("is_required")
    private Boolean isRequired;

    private String settings;

    private org.springframework.web.multipart.MultipartFile file;

    private List<QuestionOptionRequestDTO> options;
}
