package com.example.Service.Impl;

import com.example.DTO.Request.QuizRoomRequestDTO;
import com.example.DTO.Response.QuizRoomResponseDTO;
import com.example.Entity.QuizRoom;
import com.example.Enum.RoomStatus;
import com.example.Exception.ResourceNotFoundException;
import com.example.Mapper.QuizRoomMapper;
import com.example.Repository.QuizRoomRepository;
import com.example.Service.QuizRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.security.SecureRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class QuizRoomServiceImpl implements QuizRoomService {

    private final QuizRoomRepository quizRoomRepository;
    private final QuizRoomMapper quizRoomMapper;

    @Override
    public QuizRoomResponseDTO createRoom(QuizRoomRequestDTO dto) {
        QuizRoom entity = quizRoomMapper.toEntity(dto);
        if (entity.getRoom_code() == null || entity.getRoom_code().isEmpty()) {
            entity.setRoom_code(generateRoomCode());
        }
        if (entity.getStatus() == null) {
            entity.setStatus(RoomStatus.OPEN);
        }
        if (entity.getCurrent_participants() == null) {
            entity.setCurrent_participants(0);
        }
        QuizRoom saved = quizRoomRepository.save(entity);
        return quizRoomMapper.toResponseDTO(saved);
    }

    @Override
    public QuizRoomResponseDTO updateRoom(UUID id, QuizRoomRequestDTO dto) {
        QuizRoom existing = quizRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizRoom", "id", id));
        quizRoomMapper.updateEntityFromDTO(existing, dto);
        QuizRoom updated = quizRoomRepository.save(existing);
        return quizRoomMapper.toResponseDTO(updated);
    }

    @Override
    public void deleteRoom(UUID id) {
        QuizRoom existing = quizRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizRoom", "id", id));
        quizRoomRepository.delete(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizRoomResponseDTO getRoomById(UUID id) {
        QuizRoom existing = quizRoomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizRoom", "id", id));
        return quizRoomMapper.toResponseDTO(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizRoomResponseDTO getRoomByCode(String code) {
        QuizRoom existing = quizRoomRepository.findByRoom_code(code)
                .orElseThrow(() -> new ResourceNotFoundException("QuizRoom", "code", code));
        return quizRoomMapper.toResponseDTO(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.DTO.Response.PaginatedData<QuizRoomResponseDTO> getRoomsByQuizId(UUID quizId, RoomStatus status,
            int page, int limit) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(Math.max(page - 1, 0), limit, org.springframework.data.domain.Sort
                        .by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        org.springframework.data.domain.Page<QuizRoom> roomPage;
        if (status != null) {
            roomPage = quizRoomRepository.findByQuizIdAndStatus(quizId, status, pageable);
        } else {
            roomPage = quizRoomRepository.findByQuizId(quizId, pageable);
        }
        return toPaginatedData(roomPage);
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.DTO.Response.PaginatedData<QuizRoomResponseDTO> getRoomsByQuizSlug(String slug,
            RoomStatus status, int page, int limit) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                .of(Math.max(page - 1, 0), limit, org.springframework.data.domain.Sort
                        .by(org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));
        org.springframework.data.domain.Page<QuizRoom> roomPage;
        if (status != null) {
            roomPage = quizRoomRepository.findByQuizSlugAndStatus(slug, status, pageable);
        } else {
            roomPage = quizRoomRepository.findByQuizSlug(slug, pageable);
        }
        return toPaginatedData(roomPage);
    }

    private com.example.DTO.Response.PaginatedData<QuizRoomResponseDTO> toPaginatedData(
            org.springframework.data.domain.Page<QuizRoom> roomPage) {
        List<QuizRoomResponseDTO> items = quizRoomMapper.toResponseDTOList(roomPage.getContent());
        com.example.DTO.Response.PaginationMeta meta = com.example.DTO.Response.PaginationMeta.builder()
                .page(roomPage.getNumber() + 1)
                .limit(roomPage.getSize())
                .total(roomPage.getTotalElements())
                .totalItems(roomPage.getTotalElements())
                .totalPages(roomPage.getTotalPages())
                .hasNext(roomPage.hasNext())
                .hasPrev(roomPage.hasPrevious())
                .build();

        return com.example.DTO.Response.PaginatedData.<QuizRoomResponseDTO>builder()
                .items(items)
                .meta(meta)
                .build();
    }

    private String generateRoomCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }
}
