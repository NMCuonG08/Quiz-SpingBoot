package com.example.Service.Impl;

import com.example.DTO.Request.QuizSessionRequestDTO;
import com.example.DTO.Response.QuizSessionResponseDTO;
import com.example.Entity.QuizSession;
import com.example.Exception.ResourceNotFoundException;
import com.example.Mapper.QuizSessionMapper;
import com.example.Repository.QuizSessionRepository;
import com.example.Service.QuizSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class QuizSessionServiceImpl implements QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;
    private final QuizSessionMapper quizSessionMapper;
    private final com.example.Repository.QuizRepository quizRepository;
    private final com.example.Repository.QuizAttemptRepository quizAttemptRepository;
    private final com.example.Mapper.QuizAttemptMapper quizAttemptMapper;
    private final com.example.Repository.QuestionRepository questionRepository;
    private final com.example.Repository.QuestionResponseRepository questionResponseRepository;

    @Autowired
    public QuizSessionServiceImpl(QuizSessionRepository quizSessionRepository,
            QuizSessionMapper quizSessionMapper,
            com.example.Repository.QuizRepository quizRepository,
            com.example.Repository.QuizAttemptRepository quizAttemptRepository,
            com.example.Mapper.QuizAttemptMapper quizAttemptMapper,
            com.example.Repository.QuestionRepository questionRepository,
            com.example.Repository.QuestionResponseRepository questionResponseRepository) {
        this.quizSessionRepository = quizSessionRepository;
        this.quizSessionMapper = quizSessionMapper;
        this.quizRepository = quizRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.quizAttemptMapper = quizAttemptMapper;
        this.questionRepository = questionRepository;
        this.questionResponseRepository = questionResponseRepository;
    }

    @Override
    public QuizSessionResponseDTO createQuizSession(QuizSessionRequestDTO dto) {
        QuizSession session = quizSessionMapper.toEntity(dto);
        return quizSessionMapper.toResponseDTO(quizSessionRepository.save(session));
    }

    @Override
    public QuizSessionResponseDTO updateQuizSession(UUID id, QuizSessionRequestDTO dto) {
        QuizSession existingSession = quizSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizSession", "id", id));
        quizSessionMapper.updateEntityFromRequestDTO(existingSession, dto);
        return quizSessionMapper.toResponseDTO(quizSessionRepository.save(existingSession));
    }

    @Override
    public void deleteQuizSession(UUID id) {
        QuizSession existingSession = quizSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizSession", "id", id));
        existingSession.setIsDeleted(true); // Soft delete
        quizSessionRepository.save(existingSession);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizSessionResponseDTO getQuizSessionById(UUID id) {
        QuizSession session = quizSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuizSession", "id", id));
        return quizSessionMapper.toResponseDTO(session);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSessionResponseDTO> getAllQuizSessions() {
        return quizSessionMapper.toResponseDTOList(quizSessionRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSessionResponseDTO> getQuizSessionsByHostId(UUID hostId) {
        return quizSessionMapper.toResponseDTOList(quizSessionRepository.findByHostIdAndIsDeletedFalse(hostId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizSessionResponseDTO> getQuizSessionsByQuizId(UUID quizId) {
        return quizSessionMapper.toResponseDTOList(quizSessionRepository.findByQuizIdAndIsDeletedFalse(quizId));
    }

    @Override
    public com.example.DTO.Response.QuizSessionPublicResponseDTO createOrResumePublicSession(String slug, UUID userId) {
        com.example.Entity.Quiz quiz = quizRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "slug", slug));

        // Check for an existing in-progress attempt for this user and quiz
        com.example.Entity.QuizAttempt attempt = quizAttemptRepository
                .findFirstByQuiz_idAndUser_idAndStatusOrderByCreatedAtDesc(quiz.getId(), userId,
                        com.example.Enum.AttemptStatus.IN_PROGRESS)
                .orElse(null);

        boolean isResume = true;
        if (attempt == null) {
            isResume = false;
            attempt = new com.example.Entity.QuizAttempt();
            attempt.setQuiz_id(quiz.getId());
            attempt.setUser_id(userId);
            attempt.setStatus(com.example.Enum.AttemptStatus.IN_PROGRESS);
            attempt.setStarted_at(java.time.LocalDateTime.now());
            attempt.setScore(0.0);
            attempt.setTime_taken(0);
            attempt = quizAttemptRepository.save(attempt);
        }

        return com.example.DTO.Response.QuizSessionPublicResponseDTO.builder()
                .id(attempt.getId())
                .quizId(quiz.getId())
                .userId(userId)
                .startedAt(attempt.getStarted_at())
                .currentQuestionIndex(0)
                .totalQuestions(quiz.getQuestions() != null ? quiz.getQuestions().size() : 0)
                .score(attempt.getScore())
                .timeSpent(attempt.getTime_taken())
                .answers(new java.util.ArrayList<>())
                .isResume(isResume)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.example.DTO.Response.QuizAttemptResponseDTO> getUserAttempts(UUID userId) {
        return quizAttemptMapper.toResponseDTOList(quizAttemptRepository.findByUser_idOrderByCreatedAtDesc(userId));
    }

    @Override
    public com.example.DTO.Response.QuizAttemptResponseDTO completePublicQuizSession(UUID attemptId) {
        com.example.Entity.QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt", "id", attemptId));

        if (attempt.getStatus() == com.example.Enum.AttemptStatus.COMPLETED) {
            return quizAttemptMapper.toResponseDTO(attempt);
        }

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        attempt.setCompleted_at(now);
        attempt.setStatus(com.example.Enum.AttemptStatus.COMPLETED);

        // Calculate time taken in seconds
        if (attempt.getStarted_at() != null) {
            long seconds = java.time.Duration.between(attempt.getStarted_at(), now).getSeconds();
            attempt.setTime_taken((int) seconds);
        }

        // Calculate score from responses
        double totalScore = 0;
        if (attempt.getResponses() != null) {
            for (com.example.Entity.QuestionResponse response : attempt.getResponses()) {
                if (response.getPoints_earned() != null) {
                    totalScore += response.getPoints_earned();
                }
            }
        }
        attempt.setScore(totalScore);

        // Calculate percentage and passed status
        com.example.Entity.Quiz quiz = attempt.getQuiz();
        if (quiz != null) {
            // Calculate max score
            double maxScore = 0;
            if (quiz.getQuestions() != null) {
                for (com.example.Entity.Question q : quiz.getQuestions()) {
                    if (q.getPoints() != null) {
                        maxScore += q.getPoints();
                    }
                }
            }
            attempt.setMax_score(maxScore);

            if (maxScore > 0) {
                attempt.setPercentage((totalScore / maxScore) * 100);
            } else {
                attempt.setPercentage(0.0);
            }

            if (quiz.getPassing_score() != null) {
                attempt.setPassed(totalScore >= quiz.getPassing_score());
            } else {
                attempt.setPassed(true); // Default to pass if no score set
            }
        }

        return quizAttemptMapper.toResponseDTO(quizAttemptRepository.save(attempt));
    }

    @Override
    public void submitPublicAnswer(UUID attemptId, com.example.DTO.Request.QuizAnswerPublicRequestDTO answerBody) {
        com.example.Entity.QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt", "id", attemptId));

        if (attempt.getStatus() != com.example.Enum.AttemptStatus.IN_PROGRESS) {
            throw new IllegalStateException("Attempt is no longer in progress");
        }

        com.example.Entity.Question question = questionRepository.findById(answerBody.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", answerBody.getQuestionId()));

        com.example.Entity.QuestionResponse response = questionResponseRepository
                .findByAttempt_idAndQuestion_id(attemptId, answerBody.getQuestionId())
                .orElse(new com.example.Entity.QuestionResponse());

        response.setAttempt_id(attemptId);
        response.setQuestion_id(answerBody.getQuestionId());

        // Support both singular and plural fields from user payload
        java.util.List<String> selectedOptionsIds = answerBody.getSelectedOptionIds();
        if (selectedOptionsIds == null && answerBody.getSelectedOptionId() != null) {
            selectedOptionsIds = java.util.Collections.singletonList(answerBody.getSelectedOptionId());
        }

        response.setSelected_options(selectedOptionsIds);
        response.setText_answer(answerBody.getTextAnswer());
        response.setTime_taken(answerBody.getTimeSpent());
        response.setAnswered_at(
                answerBody.getAnsweredAt() != null ? answerBody.getAnsweredAt() : java.time.LocalDateTime.now());

        // Check if values are provided in payload, if not calculate them
        if (answerBody.getIsCorrect() != null) {
            response.setIs_correct(answerBody.getIsCorrect());
            response.setPoints_earned(answerBody.getPointsEarned() != null ? answerBody.getPointsEarned()
                    : (answerBody.getIsCorrect() ? (question.getPoints() != null ? question.getPoints() : 1.0) : 0.0));
        } else {
            // Basic scoring for single/multiple choice
            boolean isCorrect = false;
            if (question.getQuestion_type() != null &&
                    (question.getQuestion_type() == com.example.Enum.QuestionType.SINGLE_CHOICE ||
                            question.getQuestion_type() == com.example.Enum.QuestionType.MULTIPLE_CHOICE)) {

                java.util.List<String> correctOptionIds = question.getOptions().stream()
                        .filter(com.example.Entity.QuestionOption::getIs_correct)
                        .map(o -> o.getId().toString())
                        .toList();

                java.util.List<String> finalSelectedIds = selectedOptionsIds != null ? selectedOptionsIds
                        : new java.util.ArrayList<>();

                if (!correctOptionIds.isEmpty() && correctOptionIds.size() == finalSelectedIds.size() &&
                        new java.util.HashSet<>(correctOptionIds).containsAll(finalSelectedIds)) {
                    isCorrect = true;
                }
            }
            response.setIs_correct(isCorrect);
            response.setPoints_earned(isCorrect ? (question.getPoints() != null ? question.getPoints() : 1.0) : 0.0);
        }

        questionResponseRepository.save(response);
    }
}
