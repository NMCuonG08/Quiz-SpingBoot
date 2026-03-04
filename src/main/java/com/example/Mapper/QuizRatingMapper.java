package com.example.Mapper;

import com.example.DTO.Request.QuizRatingRequestDTO;
import com.example.DTO.Response.QuizRatingResponseDTO;
import com.example.Entity.QuizRating;
import com.example.Entity.User;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuizRatingMapper {

    public QuizRating toEntity(QuizRatingRequestDTO dto) {
        if (dto == null)
            return null;
        QuizRating entity = new QuizRating();
        entity.setQuiz_id(dto.getQuizId());
        entity.setUser_id(dto.getUserId());
        entity.setRating(dto.getRating());
        entity.setComment(dto.getComment());
        return entity;
    }

    public QuizRatingResponseDTO toResponseDTO(QuizRating entity) {
        if (entity == null)
            return null;

        String userName = "Anonymous";
        String userAvatar = null;

        User user = entity.getUser();
        if (user != null) {
            userName = user.getFull_name();
            // If user has thumbnail reference... looking for it...
            // Standard User has Image relationship in our system.
        }

        return QuizRatingResponseDTO.builder()
                .id(entity.getId())
                .quizId(entity.getQuiz_id())
                .userId(entity.getUser_id())
                .userName(userName)
                .userAvatar(userAvatar)
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<QuizRatingResponseDTO> toResponseDTOList(List<QuizRating> entities) {
        if (entities == null)
            return null;
        return entities.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    public void updateEntityFromDTO(QuizRating entity, QuizRatingRequestDTO dto) {
        if (entity == null || dto == null)
            return;
        entity.setRating(dto.getRating());
        entity.setComment(dto.getComment());
    }
}
