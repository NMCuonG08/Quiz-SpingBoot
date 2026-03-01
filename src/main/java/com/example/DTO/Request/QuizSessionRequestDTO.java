package com.example.DTO.Request;

import com.example.Enum.SessionStatus;
import com.example.Enum.SessionType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class QuizSessionRequestDTO {
    private UUID quizId;
    private String sessionCode;
    private UUID hostId;
    private SessionType sessionType;
    private SessionStatus status;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String settings;
}
