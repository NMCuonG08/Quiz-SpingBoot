package com.example.DTO.Response;

import com.example.Enum.FeedbackStatus;
import com.example.Enum.FeedbackType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDTO {

    private UUID id;
    private FeedbackType type;
    private String subject;
    private String message;
    private Integer rating;
    private FeedbackStatus status;
    private String adminResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Thông tin user gửi feedback
    private UserSummary user;

    // Thông tin quiz (nếu có)
    private QuizSummary quiz;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private UUID id;
        private String username;
        private String fullName;
        private String avatar;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuizSummary {
        private UUID id;
        private String title;
    }
}
