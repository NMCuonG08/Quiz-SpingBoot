package com.example.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.UUID;

@Data
public class QuizSessionPublicRequestDTO {
    @JsonProperty("quiz_slug")
    private String quizSlug;

    @JsonProperty("user_id")
    private UUID userId;
}
