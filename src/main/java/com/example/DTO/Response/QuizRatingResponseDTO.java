package com.example.DTO.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class QuizRatingResponseDTO {
    private UUID id;

    @JsonProperty("quiz_id")
    private UUID quizId;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("user_avatar")
    private String userAvatar;

    private Integer rating;
    private String comment;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
