package com.example.Service;

import com.example.DTO.Request.QuestionOptionRequestDTO;
import com.example.DTO.Response.QuestionOptionResponseDTO;

import java.util.List;
import java.util.UUID;

public interface QuestionOptionService {
    QuestionOptionResponseDTO createQuestionOption(QuestionOptionRequestDTO dto);

    QuestionOptionResponseDTO updateQuestionOption(UUID id, QuestionOptionRequestDTO dto);

    void deleteQuestionOption(UUID id);

    QuestionOptionResponseDTO getQuestionOptionById(UUID id);

    List<QuestionOptionResponseDTO> getQuestionOptionsByQuestionId(UUID questionId);
}
