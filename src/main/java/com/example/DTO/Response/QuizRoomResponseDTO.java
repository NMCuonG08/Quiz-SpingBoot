package com.example.DTO.Response;

import com.example.Enum.RoomStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class QuizRoomResponseDTO {
    private UUID id;

    @JsonProperty("quiz_id")
    private UUID quizId;

    @JsonProperty("owner_id")
    private UUID ownerId;

    @JsonProperty("room_code")
    private String roomCode;

    private RoomStatus status;

    @JsonProperty("is_private")
    private Boolean isPrivate;

    @JsonProperty("password_hash")
    private String passwordHash;

    @JsonProperty("max_participants")
    private Integer maxParticipants;

    @JsonProperty("current_participants")
    private Integer currentParticipants;

    private String settings;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
