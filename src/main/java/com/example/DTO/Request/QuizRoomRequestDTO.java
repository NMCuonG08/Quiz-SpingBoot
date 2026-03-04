package com.example.DTO.Request;

import com.example.Enum.RoomStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class QuizRoomRequestDTO {
    @JsonProperty("quiz_id")
    private UUID quizId;

    @JsonProperty("owner_id")
    private UUID ownerId;

    @JsonProperty("room_code")
    private String roomCode;

    private RoomStatus status;

    @JsonProperty("is_private")
    private Boolean isPrivate;

    private String password;

    @JsonProperty("max_participants")
    private Integer maxParticipants;

    private String settings;
}
