package com.example.Service.Impl;

import com.example.DTO.Request.QuizRequestDTO;
import com.example.DTO.Response.QuizResponseDTO;
import com.example.Entity.Quiz;
import com.example.Exception.ResourceNotFoundException;
import com.example.Mapper.QuizMapper;
import com.example.Repository.QuizRepository;
import com.example.Service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        existingQuiz.setIsDeleted(true);
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

    private com.example.DTO.Response.PaginatedData<QuizResponseDTO> toPaginatedData(Page<Quiz> quizPage) {
        List<QuizResponseDTO> items = quizMapper.toResponseDTOList(quizPage.getContent());
        com.example.DTO.Response.PaginationMeta paginationMeta = com.example.DTO.Response.PaginationMeta.builder()
                .page(quizPage.getNumber() + 1)
                .limit(quizPage.getSize())
                .total(quizPage.getTotalElements())
                .totalItems(quizPage.getTotalElements())
                .totalPages(quizPage.getTotalPages())
                .hasNext(quizPage.hasNext())
                .hasPrev(quizPage.hasPrevious())
                .build();

        return com.example.DTO.Response.PaginatedData.<QuizResponseDTO>builder()
                .items(items)
                .pagination(paginationMeta)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.DTO.Response.PaginatedData<QuizResponseDTO> getMyQuizzes(UUID creatorId, int page, int limit) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return toPaginatedData(quizRepository.findByCreatorId(creatorId, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.DTO.Response.PaginatedData<QuizResponseDTO> getRecentlyPublished(int page, int limit) {
        // recently published: Sort by createdAt desc
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return toPaginatedData(quizRepository.findPublicQuizzes(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.DTO.Response.PaginatedData<QuizResponseDTO> getPopular(int page, int limit) {
        // popular: Sort by max_attempts desc proxy for popular (since no views/plays
        // count in quiz table directly without joining attempts)
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), limit, Sort.by(Sort.Direction.DESC, "max_attempts"));
        return toPaginatedData(quizRepository.findPublicQuizzes(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.DTO.Response.PaginatedData<QuizResponseDTO> getBestRated(int page, int limit) {
        // best rated: Sort by passing_score desc as proxy for now (since no rating
        // column directly on Quiz)
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), limit, Sort.by(Sort.Direction.DESC, "passing_score"));
        return toPaginatedData(quizRepository.findPublicQuizzes(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.DTO.Response.PaginatedData<QuizResponseDTO> getEasy(int page, int limit) {
        // easy quizzes
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return toPaginatedData(
                quizRepository.findPublicQuizzesByDifficulty(com.example.Enum.DifficultyLevel.EASY, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.DTO.Response.PaginatedData<QuizResponseDTO> getMedium(int page, int limit) {
        // medium quizzes
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return toPaginatedData(
                quizRepository.findPublicQuizzesByDifficulty(com.example.Enum.DifficultyLevel.MEDIUM, pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.DTO.Response.PaginatedData<QuizResponseDTO> getHard(int page, int limit) {
        // hard quizzes
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return toPaginatedData(
                quizRepository.findPublicQuizzesByDifficulty(com.example.Enum.DifficultyLevel.HARD, pageable));
    }
}
