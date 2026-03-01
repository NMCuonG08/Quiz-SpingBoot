package com.example.Service.Impl;

import com.example.DTO.Request.QuizRequestDTO;
import com.example.DTO.Response.QuizResponseDTO;
import com.example.Entity.Quiz;
import com.example.Exception.ResourceNotFoundException;
import com.example.Mapper.QuizMapper;
import com.example.Repository.QuizRepository;
import com.example.Service.QuizService;
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
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;
    private final ImageService imageService;

    @Autowired
    public QuizServiceImpl(QuizRepository quizRepository, QuizMapper quizMapper, ImageService imageService) {
        this.quizRepository = quizRepository;
        this.quizMapper = quizMapper;
        this.imageService = imageService;
    }

    @Override
    public QuizResponseDTO createQuiz(QuizRequestDTO quizRequestDTO) {
        MultipartFile file = quizRequestDTO.getFile();
        if (file != null && !file.isEmpty()) {
            try {
                Image image = imageService.uploadImage(file);
                quizRequestDTO.setThumbnailId(image.getId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        Quiz quiz = quizMapper.toEntity(quizRequestDTO);
        Quiz savedQuiz = quizRepository.save(quiz);
        return quizMapper.toResponseDTO(savedQuiz);
    }

    @Override
    public QuizResponseDTO updateQuiz(UUID id, QuizRequestDTO quizRequestDTO) {
        MultipartFile file = quizRequestDTO.getFile();
        if (file != null && !file.isEmpty()) {
            try {
                Image image = imageService.uploadImage(file);
                quizRequestDTO.setThumbnailId(image.getId());
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        quizMapper.updateEntityFromRequestDTO(existingQuiz, quizRequestDTO);
        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        return quizMapper.toResponseDTO(updatedQuiz);
    }

    @Override
    public void deleteQuiz(UUID id) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        existingQuiz.setIsDeleted(true); // Assuming soft delete from BaseEntity is the standard handling
        quizRepository.save(existingQuiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponseDTO getQuizById(UUID id) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        return quizMapper.toResponseDTO(existingQuiz);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponseDTO> getAllQuizzes() {
        return quizMapper.toResponseDTOList(quizRepository.findAll());
    }
}
