package com.example.Mapper;

import com.example.DTO.Request.QuizRequestDTO;
import com.example.DTO.Response.QuizResponseDTO;
import com.example.Entity.Quiz;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuizMapper {

    public Quiz toEntity(QuizRequestDTO quizRequestDTO) {
        if (quizRequestDTO == null)
            return null;

        Quiz quiz = new Quiz();
        updateEntityFromRequestDTO(quiz, quizRequestDTO);
        return quiz;
    }

    public QuizResponseDTO toResponseDTO(Quiz quiz) {
        if (quiz == null)
            return null;

        return QuizResponseDTO.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .slug(quiz.getSlug())
                .description(quiz.getDescription())
                .categoryId(quiz.getCategory_id())
                .creatorId(quiz.getCreator_id())
                .difficultyLevel(quiz.getDifficulty_level())
                .timeLimit(quiz.getTime_limit())
                .maxAttempts(quiz.getMax_attempts())
                .passingScore(quiz.getPassing_score())
                .isPublic(quiz.getIs_public())
                .isActive(quiz.getIs_active())
                .quizType(quiz.getQuiz_type())
                .instructions(quiz.getInstructions())
                .thumbnailId(quiz.getThumbnail_id())
                .tags(quiz.getTags())
                .settings(quiz.getSettings())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }

    public void updateEntityFromRequestDTO(Quiz quiz, QuizRequestDTO dto) {
        if (quiz == null || dto == null)
            return;

        quiz.setTitle(dto.getTitle());
        quiz.setSlug(dto.getSlug());
        quiz.setDescription(dto.getDescription());
        quiz.setCategory_id(dto.getCategoryId());
        quiz.setCreator_id(dto.getCreatorId());
        quiz.setDifficulty_level(dto.getDifficultyLevel());
        quiz.setTime_limit(dto.getTimeLimit());
        quiz.setMax_attempts(dto.getMaxAttempts());
        quiz.setPassing_score(dto.getPassingScore());
        quiz.setIs_public(dto.getIsPublic());
        quiz.setIs_active(dto.getIsActive());
        quiz.setQuiz_type(dto.getQuizType());
        quiz.setInstructions(dto.getInstructions());
        quiz.setThumbnail_id(dto.getThumbnailId());
        quiz.setTags(dto.getTags());
        quiz.setSettings(dto.getSettings());
    }

    public List<QuizResponseDTO> toResponseDTOList(List<Quiz> quizzes) {
        if (quizzes == null)
            return null;
        return quizzes.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
