package com.example.Service;

import com.example.DTO.Request.QuestionRequestDTO;
import com.example.DTO.Response.QuestionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface QuestionService {
    QuestionResponseDTO createQuestion(QuestionRequestDTO questionRequestDTO);

    QuestionResponseDTO updateQuestion(UUID id, QuestionRequestDTO questionRequestDTO);

    void deleteQuestion(UUID id);

    QuestionResponseDTO getQuestionById(UUID id);

    List<QuestionResponseDTO> getQuestionsByQuizId(UUID quizId);
}
