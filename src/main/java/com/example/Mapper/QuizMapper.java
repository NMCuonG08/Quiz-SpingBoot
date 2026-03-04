package com.example.Mapper;

import com.example.DTO.Request.QuizRequestDTO;
import com.example.DTO.Response.QuizResponseDTO;
import com.example.Entity.Quiz;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuizMapper {

    @org.springframework.beans.factory.annotation.Autowired
    private com.example.Repository.UserRepository userRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.example.Repository.CategoryRepository categoryRepository;

    @org.springframework.beans.factory.annotation.Autowired
    private com.example.Repository.ImageRepository imageRepository;

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

        String creatorName = "Anonymous";
        if (quiz.getCreator() != null) {
            creatorName = quiz.getCreator().getFull_name();
        } else if (quiz.getCreator_id() != null) {
            creatorName = userRepository.findById(quiz.getCreator_id())
                    .map(u -> u.getFull_name()).orElse("Anonymous");
        }

        String categoryName = "Uncategorized";
        if (quiz.getCategory() != null) {
            categoryName = quiz.getCategory().getName();
        } else if (quiz.getCategory_id() != null) {
            categoryName = categoryRepository.findById(quiz.getCategory_id())
                    .map(c -> c.getName()).orElse("Uncategorized");
        }

        String thumbUrl = null;
        if (quiz.getThumbnail() != null) {
            thumbUrl = quiz.getThumbnail().getUrl();
        } else if (quiz.getThumbnail_id() != null) {
            thumbUrl = imageRepository.findById(quiz.getThumbnail_id())
                    .map(i -> i.getUrl()).orElse(null);
        }

        int qsCount = 0;
        if (quiz.getQuestions() != null) {
            qsCount = quiz.getQuestions().size();
        }

        int attCount = 0;
        if (quiz.getAttempts() != null) {
            attCount = quiz.getAttempts().size();
        }

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
                .thumbnailUrl(thumbUrl)
                .tags(quiz.getTags())
                .settings(quiz.getSettings())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .creatorName(creatorName)
                .categoryName(categoryName)
                .questionsCount(qsCount)
                .attemptsCount(attCount)
                .averageRating(0.0) // Mock rating to prevent frontend toFixed crash
                .totalRatings(0) // Mock format handling crash
                .build();
    }

    public void updateEntityFromRequestDTO(Quiz quiz, QuizRequestDTO dto) {
        if (quiz == null || dto == null)
            return;

        quiz.setTitle(dto.getTitle());
        quiz.setSlug(dto.getSlug());
        quiz.setDescription(dto.getDescription());
        quiz.setCategory_id(dto.getCategory_id());
        quiz.setCreator_id(dto.getCreator_id());
        quiz.setDifficulty_level(dto.getDifficulty_level());
        quiz.setTime_limit(dto.getTime_limit());
        quiz.setMax_attempts(dto.getMax_attempts());
        quiz.setPassing_score(dto.getPassing_score());
        quiz.setIs_public(dto.getIs_public() != null ? dto.getIs_public() : true);
        quiz.setIs_active(dto.getIs_active() != null ? dto.getIs_active() : true);
        quiz.setQuiz_type(dto.getQuiz_type());
        quiz.setInstructions(dto.getInstructions());
        quiz.setThumbnail_id(dto.getThumbnail_id());
        quiz.setTags(dto.getTags());
        quiz.setSettings(dto.getSettings());
    }

    public List<QuizResponseDTO> toResponseDTOList(List<Quiz> quizzes) {
        if (quizzes == null)
            return null;
        return quizzes.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
