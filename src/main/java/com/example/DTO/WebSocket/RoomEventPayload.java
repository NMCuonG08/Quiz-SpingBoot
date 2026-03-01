package com.example.DTO.WebSocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

/**
 * Payload cho các sự kiện liên quan đến Room/Session
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomEventPayload {
    private UUID sessionId;
    private String sessionCode;
    private UUID userId;
    private String username;
    private String avatar;
    private Object data; // Extra data (answer, question, etc.)
    private int participantCount; // Số người trong phòng
}
