package com.example.Service.Impl;

import com.example.DTO.Request.QuizSessionRequestDTO;
import com.example.DTO.Response.QuizSessionResponseDTO;
import com.example.Entity.QuizSession;
import com.example.Exception.ResourceNotFoundException;
import com.example.Mapper.QuizSessionMapper;
import com.example.Repository.QuizSessionRepository;
import com.example.Service.QuizSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class QuizSessionServiceImpl implements QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;
    private final QuizSessionMapper quizSessionMapper;

    @Autowired
    public QuizSessionServiceImpl(QuizSessionRepository quizSessionRepository, QuizSessionMapper quizSessionMapper) {
        this.quizSessionRepository = quizSessionRepository;
        this.quizSessionMapper = quizSessionMapper;
    }

    @Override
    public QuizSessionResponseDTO createQuizSession(QuizSessionRequestDTO dto) {
        QuizSession session = quizSessionMapper.toEntity(dto);
        return quizSessionMapper.toResponseDTO(quizSessionRepository.save(session));
    }

    @Override
    public QuizSessionResponseDTO updateQuizSession(UUID id, QuizSessionRequestDTO dto) {
        QuizSession existingSession = quizSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizSession", "id", id));
        quizSessionMapper.updateEntityFromRequestDTO(existingSession, dto);
        return quizSessionMapper.toResponseDTO(quizSessionRepository.save(existingSession));
    }

    @Override
    public void deleteQuizSession(UUID id) {
        QuizSession existingSession = quizSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizSession", "id", id));
        existingSession.setIsDeleted(true); // Soft delete
        quizSessionRepository.save(existingSession);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizSessionResponseDTO getQuizSessionById(UUID id) {
        QuizSession session = quizSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizSession", "id", id));
        return quizSessionMapper.toResponseDTO(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSessionResponseDTO> getAllQuizSessions() {
        return quizSessionMapper.toResponseDTOList(quizSessionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSessionResponseDTO> getQuizSessionsByHostId(UUID hostId) {
        return quizSessionMapper.toResponseDTOList(quizSessionRepository.findByHostIdAndIsDeletedFalse(hostId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSessionResponseDTO> getQuizSessionsByQuizId(UUID quizId) {
        return quizSessionMapper.toResponseDTOList(quizSessionRepository.findByQuizIdAndIsDeletedFalse(quizId));
    }
}
