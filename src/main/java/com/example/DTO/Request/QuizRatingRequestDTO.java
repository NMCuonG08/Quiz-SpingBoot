package com.example.DTO.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class QuizRatingRequestDTO {
    @NotNull(message = "quiz_id is required")
    @JsonProperty("quiz_id")
    private UUID quizId;

    @JsonProperty("user_id")
    private UUID userId;

    @Min(1)
    @Max(5)
    @NotNull(message = "rating is required")
    private Integer rating;

    @NotBlank(message = "comment is required")
    private String comment;
}
