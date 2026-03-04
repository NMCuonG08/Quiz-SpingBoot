package com.example.DTO.Request;

import com.example.Enum.DifficultyLevel;
import com.example.Enum.QuizType;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class QuizRequestDTO {
    private String title;
    private String slug;
    private String description;
    private UUID category_id;
    private UUID creator_id;
    private DifficultyLevel difficulty_level;
    private Integer time_limit;
    private Integer max_attempts;
    private Double passing_score;
    private Boolean is_public;
    private Boolean is_active;
    private QuizType quiz_type;
    private String instructions;
    private UUID thumbnail_id;
    private List<String> tags;
    private String settings;
    private org.springframework.web.multipart.MultipartFile file;
}
