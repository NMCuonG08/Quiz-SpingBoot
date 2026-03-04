package com.example.Mapper;

import com.example.DTO.Request.QuestionRequestDTO;
import com.example.DTO.Response.QuestionResponseDTO;
import com.example.Entity.Question;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {

    private final QuestionOptionMapper optionMapper;

    public QuestionMapper(QuestionOptionMapper optionMapper) {
        this.optionMapper = optionMapper;
    }

    public Question toEntity(QuestionRequestDTO dto) {
        if (dto == null)
            return null;
        Question question = new Question();
        updateEntityFromRequestDTO(question, dto);
        return question;
    }

    public QuestionResponseDTO toResponseDTO(Question question) {
        if (question == null)
            return null;
        return QuestionResponseDTO.builder()
                .id(question.getId())
                .quizId(question.getQuiz_id())
                .questionText(question.getQuestion_text())
                .slug(question.getSlug())
                .questionType(question.getQuestion_type())
                .points(question.getPoints())
                .timeLimit(question.getTime_limit())
                .explanation(question.getExplanation())
                .mediaId(question.getMedia_id())
                .mediaType(question.getMedia_type())
                .difficultyLevel(question.getDifficulty_level())
                .sortOrder(question.getSort_order())
                .isRequired(question.getIs_required())
                .settings(question.getSettings())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .content(question.getQuestion_text())
                .quizTitle(question.getQuiz() != null ? question.getQuiz().getTitle() : "")
                .optionsCount(question.getOptions() != null ? question.getOptions().size() : 0)
                .options(question.getOptions() != null ? optionMapper.toResponseDTOList(question.getOptions()) : null)
                .build();
    }

    public void updateEntityFromRequestDTO(Question question, QuestionRequestDTO dto) {
        if (question == null || dto == null)
            return;
        question.setQuiz_id(dto.getQuizId());
        question.setQuestion_text(dto.getQuestionText());
        question.setSlug(dto.getSlug());
        question.setQuestion_type(dto.getQuestionType());
        question.setPoints(dto.getPoints());
        question.setTime_limit(dto.getTimeLimit());
        question.setExplanation(dto.getExplanation());
        question.setMedia_id(dto.getMediaId());
        question.setMedia_type(dto.getMediaType());
        question.setDifficulty_level(dto.getDifficultyLevel());
        question.setSort_order(dto.getSortOrder());
        question.setIs_required(dto.getIsRequired());
        question.setSettings(dto.getSettings());
    }

    public List<QuestionResponseDTO> toResponseDTOList(List<Question> questions) {
        if (questions == null)
            return null;
        return questions.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
