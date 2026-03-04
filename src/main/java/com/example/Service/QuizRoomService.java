package com.example.Service;

import com.example.DTO.Request.QuizRoomRequestDTO;
import com.example.DTO.Response.QuizRoomResponseDTO;
import com.example.Enum.RoomStatus;
import java.util.List;
import java.util.UUID;

public interface QuizRoomService {
    QuizRoomResponseDTO createRoom(QuizRoomRequestDTO dto);

    QuizRoomResponseDTO updateRoom(UUID id, QuizRoomRequestDTO dto);

    void deleteRoom(UUID id);

    QuizRoomResponseDTO getRoomById(UUID id);

    QuizRoomResponseDTO getRoomByCode(String code);

    com.example.DTO.Response.PaginatedData<QuizRoomResponseDTO> getRoomsByQuizId(UUID quizId, RoomStatus status,
            int page, int limit);

    com.example.DTO.Response.PaginatedData<QuizRoomResponseDTO> getRoomsByQuizSlug(String slug, RoomStatus status,
            int page, int limit);
}
