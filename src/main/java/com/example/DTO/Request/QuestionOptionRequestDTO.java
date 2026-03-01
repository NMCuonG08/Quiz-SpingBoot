package com.example.DTO.Request;

import lombok.Data;
import java.util.UUID;

@Data
public class QuestionOptionRequestDTO {
    private UUID questionId;
    private String optionText;
    private Boolean isCorrect;
    private Integer sortOrder;
    private String explanation;
    private String mediaUrl;
}
