package com.example.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.UUID;

@Data
public class QuestionOptionRequestDTO {
    @JsonProperty("question_id")
    private UUID questionId;

    @JsonProperty("option_text")
    private String optionText;

    @JsonProperty("is_correct")
    private Boolean isCorrect;

    @JsonProperty("sort_order")
    private Integer sortOrder;

    private String explanation;

    @JsonProperty("media_url")
    private String mediaUrl;
}
