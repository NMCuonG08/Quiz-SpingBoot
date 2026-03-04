package com.example.DTO.Response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class QuestionOptionResponseDTO {
    private UUID id;

    @com.fasterxml.jackson.annotation.JsonProperty("question_id")
    private UUID questionId;

    @com.fasterxml.jackson.annotation.JsonProperty("option_text")
    private String optionText;

    private String content;

    @com.fasterxml.jackson.annotation.JsonProperty("is_correct")
    private Boolean isCorrect;

    @com.fasterxml.jackson.annotation.JsonProperty("sort_order")
    private Integer sortOrder;

    private String explanation;

    @com.fasterxml.jackson.annotation.JsonProperty("media_url")
    private String mediaUrl;

    @com.fasterxml.jackson.annotation.JsonProperty("created_at")
    private LocalDateTime createdAt;

    @com.fasterxml.jackson.annotation.JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @com.fasterxml.jackson.annotation.JsonProperty("question_text")
    private String questionText;

    @com.fasterxml.jackson.annotation.JsonProperty("question_type")
    private String questionType;
}
