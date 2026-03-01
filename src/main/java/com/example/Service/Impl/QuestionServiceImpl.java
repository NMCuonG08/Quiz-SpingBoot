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

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository, QuestionMapper questionMapper,
            ImageService imageService) {
        this.questionRepository = questionRepository;
        this.questionMapper = questionMapper;
        this.imageService = imageService;
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
    public List<QuestionResponseDTO> getQuestionsByQuizId(UUID quizId) {
        return questionMapper.toResponseDTOList(questionRepository.findByQuizId(quizId));
    }
}
