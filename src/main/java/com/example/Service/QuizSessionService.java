package com.example.Service;

import com.example.DTO.Request.QuizSessionRequestDTO;
import com.example.DTO.Response.QuizSessionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface QuizSessionService {
    QuizSessionResponseDTO createQuizSession(QuizSessionRequestDTO dto);

    QuizSessionResponseDTO updateQuizSession(UUID id, QuizSessionRequestDTO dto);

    void deleteQuizSession(UUID id);

    QuizSessionResponseDTO getQuizSessionById(UUID id);

    List<QuizSessionResponseDTO> getAllQuizSessions();

    List<QuizSessionResponseDTO> getQuizSessionsByHostId(UUID hostId);

    List<QuizSessionResponseDTO> getQuizSessionsByQuizId(UUID quizId);

    com.example.DTO.Response.QuizSessionPublicResponseDTO createOrResumePublicSession(String slug, UUID userId);

    java.util.List<com.example.DTO.Response.QuizAttemptResponseDTO> getUserAttempts(UUID userId);

    com.example.DTO.Response.QuizAttemptResponseDTO completePublicQuizSession(UUID attemptId);

    void submitPublicAnswer(UUID attemptId, com.example.DTO.Request.QuizAnswerPublicRequestDTO answerBody);
}
