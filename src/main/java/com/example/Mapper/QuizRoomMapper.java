package com.example.Mapper;

import com.example.DTO.Request.QuizRoomRequestDTO;
import com.example.DTO.Response.QuizRoomResponseDTO;
import com.example.Entity.QuizRoom;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuizRoomMapper {

    public QuizRoom toEntity(QuizRoomRequestDTO dto) {
        if (dto == null)
            return null;
        QuizRoom entity = new QuizRoom();
        updateEntityFromDTO(entity, dto);
        return entity;
    }

    public QuizRoomResponseDTO toResponseDTO(QuizRoom entity) {
        if (entity == null)
            return null;
        return QuizRoomResponseDTO.builder()
                .id(entity.getId())
                .quizId(entity.getQuiz_id())
                .ownerId(entity.getOwner_id())
                .roomCode(entity.getRoom_code())
                .status(entity.getStatus())
                .isPrivate(entity.getIs_private())
                .passwordHash(entity.getPassword_hash())
                .maxParticipants(entity.getMax_participants())
                .currentParticipants(entity.getCurrent_participants())
                .settings(entity.getSettings())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<QuizRoomResponseDTO> toResponseDTOList(List<QuizRoom> entities) {
        if (entities == null)
            return null;
        return entities.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public void updateEntityFromDTO(QuizRoom entity, QuizRoomRequestDTO dto) {
        if (entity == null || dto == null)
            return;
        entity.setQuiz_id(dto.getQuizId());
        entity.setOwner_id(dto.getOwnerId());
        entity.setRoom_code(dto.getRoomCode());
        entity.setStatus(dto.getStatus());
        entity.setIs_private(dto.getIsPrivate() != null ? dto.getIsPrivate() : false);
        entity.setMax_participants(dto.getMaxParticipants());
        entity.setSettings(dto.getSettings());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            entity.setPassword_hash(dto.getPassword()); // Hash logic if needed
        }

        if (entity.getCurrent_participants() == null) {
            entity.setCurrent_participants(0);
        }
    }
}
