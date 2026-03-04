package com.example.Service.Impl;

import com.example.DTO.Request.QuizRatingRequestDTO;
import com.example.DTO.Response.PaginatedData;
import com.example.DTO.Response.PaginationMeta;
import com.example.DTO.Response.QuizRatingResponseDTO;
import com.example.Entity.QuizRating;
import com.example.Exception.ResourceNotFoundException;
import com.example.Mapper.QuizRatingMapper;
import com.example.Repository.QuizRatingRepository;
import com.example.Service.QuizRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class QuizRatingServiceImpl implements QuizRatingService {

    private final QuizRatingRepository quizRatingRepository;
    private final QuizRatingMapper quizRatingMapper;

    @Override
    public QuizRatingResponseDTO createOrUpdateRating(QuizRatingRequestDTO dto) {
        QuizRating entity = quizRatingRepository.findByQuiz_idAndUser_id(dto.getQuizId(), dto.getUserId())
                .orElse(null);

        if (entity != null) {
            quizRatingMapper.updateEntityFromDTO(entity, dto);
        } else {
            entity = quizRatingMapper.toEntity(dto);
        }

        QuizRating saved = quizRatingRepository.save(entity);
        return quizRatingMapper.toResponseDTO(saved);
    }

    @Override
    public void deleteRating(UUID quizId, UUID userId) {
        quizRatingRepository.deleteByQuiz_idAndUser_id(quizId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizRatingResponseDTO getRatingById(UUID id) {
        QuizRating entity = quizRatingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizRating", "id", id));
        return quizRatingMapper.toResponseDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedData<QuizRatingResponseDTO> getRatingsByQuizId(UUID quizId, int page, int limit) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<QuizRating> ratingPage = quizRatingRepository.findByQuiz_id(quizId, pageable);

        List<QuizRatingResponseDTO> items = quizRatingMapper.toResponseDTOList(ratingPage.getContent());

        PaginationMeta meta = PaginationMeta.builder()
                .page(ratingPage.getNumber() + 1)
                .limit(ratingPage.getSize())
                .total(ratingPage.getTotalElements())
                .totalItems(ratingPage.getTotalElements())
                .totalPages(ratingPage.getTotalPages())
                .hasNext(ratingPage.hasNext())
                .hasPrev(ratingPage.hasPrevious())
                .build();

        return PaginatedData.<QuizRatingResponseDTO>builder()
                .items(items)
                .meta(meta)
                .build();
    }
}
