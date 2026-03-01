package com.example.Mapper;

import com.example.DTO.Request.QuestionOptionRequestDTO;
import com.example.DTO.Response.QuestionOptionResponseDTO;
import com.example.Entity.QuestionOption;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionOptionMapper {

    public QuestionOption toEntity(QuestionOptionRequestDTO dto) {
        if (dto == null)
            return null;
        QuestionOption option = new QuestionOption();
        updateEntityFromRequestDTO(option, dto);
        return option;
    }

    public QuestionOptionResponseDTO toResponseDTO(QuestionOption option) {
        if (option == null)
            return null;
        return QuestionOptionResponseDTO.builder()
                .id(option.getId())
                .questionId(option.getQuestion_id())
                .optionText(option.getOption_text())
                .isCorrect(option.getIs_correct())
                .sortOrder(option.getSort_order())
                .explanation(option.getExplanation())
                .mediaUrl(option.getMedia_url())
                .createdAt(option.getCreatedAt())
                .updatedAt(option.getUpdatedAt())
                .build();
    }

    public void updateEntityFromRequestDTO(QuestionOption option, QuestionOptionRequestDTO dto) {
        if (option == null || dto == null)
            return;
        option.setQuestion_id(dto.getQuestionId());
        option.setOption_text(dto.getOptionText());
        option.setIs_correct(dto.getIsCorrect());
        option.setSort_order(dto.getSortOrder());
        option.setExplanation(dto.getExplanation());
        option.setMedia_url(dto.getMediaUrl());
    }

    public List<QuestionOptionResponseDTO> toResponseDTOList(List<QuestionOption> options) {
        if (options == null)
            return null;
        return options.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }
}
