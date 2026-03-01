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
}
