package com.example.Service;

import com.example.DTO.Request.QuizRatingRequestDTO;
import com.example.DTO.Response.PaginatedData;
import com.example.DTO.Response.QuizRatingResponseDTO;
import java.util.UUID;

public interface QuizRatingService {
    QuizRatingResponseDTO createOrUpdateRating(QuizRatingRequestDTO dto);

    void deleteRating(UUID quizId, UUID userId);

    QuizRatingResponseDTO getRatingById(UUID id);

    PaginatedData<QuizRatingResponseDTO> getRatingsByQuizId(UUID quizId, int page, int limit);
}
