package com.example.Service.Impl;

import com.example.DTO.Request.QuestionRequestDTO;
import com.example.DTO.Response.QuestionResponseDTO;
import com.example.Entity.Question;
import com.example.Exception.ResourceNotFoundException;
import com.example.Mapper.QuestionMapper;
import com.example.Repository.QuestionRepository;
import com.example.Service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import com.example.Entity.Image;
import com.example.Service.ImageService;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final ImageService imageService;
    private final com.example.Repository.QuestionOptionRepository optionRepository;
    private final com.example.Mapper.QuestionOptionMapper optionMapper;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository,
            QuestionMapper questionMapper,
            ImageService imageService,
            com.example.Repository.QuestionOptionRepository optionRepository,
            com.example.Mapper.QuestionOptionMapper optionMapper) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
        this.imageService = imageService;
        this.optionRepository = optionRepository;
        this.optionMapper = optionMapper;
    }

    @Override
    public QuestionResponseDTO createQuestion(QuestionRequestDTO questionRequestDTO) {
        MultipartFile file = questionRequestDTO.getFile();
        if (file != null && !file.isEmpty()) {
            try {
                Image image = imageService.uploadImage(file);
                questionRequestDTO.setMediaId(image.getId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        Question question = questionMapper.toEntity(questionRequestDTO);
        Question savedQuestion = questionRepository.save(question);

        // Save options if present
        if (questionRequestDTO.getOptions() != null && !questionRequestDTO.getOptions().isEmpty()) {
            java.util.List<com.example.Entity.QuestionOption> savedOptions = new java.util.ArrayList<>();
            for (com.example.DTO.Request.QuestionOptionRequestDTO optionDTO : questionRequestDTO.getOptions()) {
                com.example.Entity.QuestionOption option = optionMapper.toEntity(optionDTO);
                option.setQuestion_id(savedQuestion.getId());
                savedOptions.add(optionRepository.save(option));
            }
            savedQuestion.setOptions(savedOptions); // Update local object for response
        }

        return questionMapper.toResponseDTO(savedQuestion);
    }

    @Override
    public QuestionResponseDTO updateQuestion(UUID id, QuestionRequestDTO questionRequestDTO) {
        MultipartFile file = questionRequestDTO.getFile();
        if (file != null && !file.isEmpty()) {
            try {
                Image image = imageService.uploadImage(file);
                questionRequestDTO.setMediaId(image.getId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
        questionMapper.updateEntityFromRequestDTO(existingQuestion, questionRequestDTO);
        Question updatedQuestion = questionRepository.save(existingQuestion);

        // Update options if present
        if (questionRequestDTO.getOptions() != null) {
            // Delete old options
            List<com.example.Entity.QuestionOption> oldOptions = optionRepository.findByQuestionId(id);
            optionRepository.deleteAll(oldOptions);

            java.util.List<com.example.Entity.QuestionOption> savedOptions = new java.util.ArrayList<>();
            for (com.example.DTO.Request.QuestionOptionRequestDTO optionDTO : questionRequestDTO.getOptions()) {
                com.example.Entity.QuestionOption option = optionMapper.toEntity(optionDTO);
                option.setQuestion_id(id);
                savedOptions.add(optionRepository.save(option));
            }
            updatedQuestion.setOptions(savedOptions); // Update local object for response
        }

        return questionMapper.toResponseDTO(updatedQuestion);
    }

    @Override
    public void deleteQuestion(UUID id) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
        existingQuestion.setIsDeleted(true); // Assuming soft delete from BaseEntity
        questionRepository.save(existingQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDTO getQuestionById(UUID id) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
        return questionMapper.toResponseDTO(existingQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.DTO.Response.PaginatedData<QuestionResponseDTO> getQuestionsByQuizId(UUID quizId, int page,
            int limit) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page - 1,
                limit);
        org.springframework.data.domain.Page<Question> questionPage = questionRepository.findByQuizId(quizId, pageable);
        return mapToPaginatedData(questionPage);
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.DTO.Response.PaginatedData<QuestionResponseDTO> getQuestionsByQuizSlug(String slug, int page,
            int limit) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page - 1,
                limit);
        org.springframework.data.domain.Page<Question> questionPage = questionRepository.findByQuizSlug(slug, pageable);
        return mapToPaginatedData(questionPage);
    }

    private com.example.DTO.Response.PaginatedData<QuestionResponseDTO> mapToPaginatedData(
            org.springframework.data.domain.Page<Question> page) {
        List<QuestionResponseDTO> items = questionMapper.toResponseDTOList(page.getContent());
        com.example.DTO.Response.PaginationMeta meta = com.example.DTO.Response.PaginationMeta.builder()
                .page(page.getNumber() + 1)
                .limit(page.getSize())
                .total(page.getTotalElements())
                .totalItems(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrev(page.hasPrevious())
                .build();
        return new com.example.DTO.Response.PaginatedData<>(items, meta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByQuizSlugPublic(String slug) {
        return questionMapper.toResponseDTOList(questionRepository.findByQuizSlugPublic(slug));
    }
}
