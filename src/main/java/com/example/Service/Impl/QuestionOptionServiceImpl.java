package com.example.Service.Impl;

import com.example.DTO.Request.QuestionOptionRequestDTO;
import com.example.DTO.Response.QuestionOptionResponseDTO;
import com.example.Entity.QuestionOption;
import com.example.Exception.ResourceNotFoundException;
import com.example.Mapper.QuestionOptionMapper;
import com.example.Repository.QuestionOptionRepository;
import com.example.Service.QuestionOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class QuestionOptionServiceImpl implements QuestionOptionService {

    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionOptionMapper questionOptionMapper;

    @Autowired
    public QuestionOptionServiceImpl(QuestionOptionRepository questionOptionRepository,
            QuestionOptionMapper questionOptionMapper) {
        this.questionOptionRepository = questionOptionRepository;
        this.questionOptionMapper = questionOptionMapper;
    }

    @Override
    public QuestionOptionResponseDTO createQuestionOption(QuestionOptionRequestDTO dto) {
        QuestionOption option = questionOptionMapper.toEntity(dto);
        return questionOptionMapper.toResponseDTO(questionOptionRepository.save(option));
    }

    @Override
    public QuestionOptionResponseDTO updateQuestionOption(UUID id, QuestionOptionRequestDTO dto) {
        QuestionOption existingOption = questionOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionOption", "id", id));
        questionOptionMapper.updateEntityFromRequestDTO(existingOption, dto);
        return questionOptionMapper.toResponseDTO(questionOptionRepository.save(existingOption));
    }

    @Override
    public void deleteQuestionOption(UUID id) {
        QuestionOption existingOption = questionOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionOption", "id", id));
        // Hard delete for options or soft delete if preferred. Using hard delete as
        // example.
        questionOptionRepository.delete(existingOption);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionOptionResponseDTO getQuestionOptionById(UUID id) {
        QuestionOption option = questionOptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionOption", "id", id));
        return questionOptionMapper.toResponseDTO(option);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionOptionResponseDTO> getQuestionOptionsByQuestionId(UUID questionId) {
        return questionOptionMapper.toResponseDTOList(questionOptionRepository.findByQuestionId(questionId));
    }
}
