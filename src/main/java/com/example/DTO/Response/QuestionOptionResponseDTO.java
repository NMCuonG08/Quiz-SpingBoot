package com.example.DTO.Response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class QuestionOptionResponseDTO {
    private UUID id;
    private UUID questionId;
    private String optionText;
    private Boolean isCorrect;
    private Integer sortOrder;
    private String explanation;
    private String mediaUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
