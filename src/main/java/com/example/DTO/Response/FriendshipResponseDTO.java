package com.example.DTO.Response;

import com.example.Enum.FriendshipStatus;
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
public class FriendshipResponseDTO {
    private UUID id;
    private FriendshipStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Thông tin người gửi lời mời
    private UserSummaryDTO sender;

    // Thông tin người nhận (the friend)
    private UserSummaryDTO receiver;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDTO {
        private UUID id;
        private String username;
        private String fullName;
        private String avatar;
    }
}
