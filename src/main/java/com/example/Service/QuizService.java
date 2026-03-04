package com.example.Service;

import com.example.DTO.Request.QuizRequestDTO;
import com.example.DTO.Response.QuizResponseDTO;

import java.util.List;
import java.util.UUID;

public interface QuizService {
    QuizResponseDTO createQuiz(QuizRequestDTO quizRequestDTO);

    QuizResponseDTO updateQuiz(UUID id, QuizRequestDTO quizRequestDTO);

    void deleteQuiz(UUID id);

    QuizResponseDTO getQuizById(UUID id);

    List<QuizResponseDTO> getAllQuizzes();

    com.example.DTO.Response.PaginatedData<QuizResponseDTO> getMyQuizzes(UUID creatorId, int page, int limit);

    com.example.DTO.Response.PaginatedData<QuizResponseDTO> getRecentlyPublished(int page, int limit);

    com.example.DTO.Response.PaginatedData<QuizResponseDTO> getPopular(int page, int limit);

    com.example.DTO.Response.PaginatedData<QuizResponseDTO> getBestRated(int page, int limit);

    com.example.DTO.Response.PaginatedData<QuizResponseDTO> getEasy(int page, int limit);

    com.example.DTO.Response.PaginatedData<QuizResponseDTO> getMedium(int page, int limit);

    com.example.DTO.Response.PaginatedData<QuizResponseDTO> getHard(int page, int limit);
}
