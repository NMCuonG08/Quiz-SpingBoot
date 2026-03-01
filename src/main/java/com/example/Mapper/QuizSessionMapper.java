package com.example.Mapper;

import com.example.DTO.Request.QuizSessionRequestDTO;
import com.example.DTO.Response.QuizSessionResponseDTO;
import com.example.Entity.QuizSession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuizSessionMapper {

    public QuizSession toEntity(QuizSessionRequestDTO dto) {
        if (dto == null)
            return null;
        QuizSession session = new QuizSession();
        updateEntityFromRequestDTO(session, dto);
        return session;
    }

    public QuizSessionResponseDTO toResponseDTO(QuizSession session) {
        if (session == null)
            return null;
        return QuizSessionResponseDTO.builder()
                .id(session.getId())
                .quizId(session.getQuiz_id())
                .sessionCode(session.getSession_code())
                .hostId(session.getHost_id())
                .sessionType(session.getSession_type())
                .status(session.getStatus())
                .maxParticipants(session.getMax_participants())
                .currentParticipants(session.getCurrent_participants())
                .startTime(session.getStart_time())
                .endTime(session.getEnd_time())
                .settings(session.getSettings())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    public void updateEntityFromRequestDTO(QuizSession session, QuizSessionRequestDTO dto) {
        if (session == null || dto == null)
            return;
        session.setQuiz_id(dto.getQuizId());
        session.setSession_code(dto.getSessionCode());
        session.setHost_id(dto.getHostId());
        session.setSession_type(dto.getSessionType());
        session.setStatus(dto.getStatus());
        session.setMax_participants(dto.getMaxParticipants());
        session.setCurrent_participants(dto.getCurrentParticipants());
        session.setStart_time(dto.getStartTime());
        session.setEnd_time(dto.getEndTime());
        session.setSettings(dto.getSettings());
    }

    public List<QuizSessionResponseDTO> toResponseDTOList(List<QuizSession> sessions) {
        if (sessions == null)
            return null;
        return sessions.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
