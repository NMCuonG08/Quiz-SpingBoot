package com.example.Service.Impl;

import com.example.DTO.Request.FeedbackAdminResponseDTO;
import com.example.DTO.Request.FeedbackRequestDTO;
import com.example.DTO.Response.FeedbackResponseDTO;
import com.example.DTO.Response.FeedbackResponseDTO.QuizSummary;
import com.example.DTO.Response.FeedbackResponseDTO.UserSummary;
import com.example.DTO.Response.PaginatedData;
import com.example.DTO.Response.PaginationMeta;
import com.example.Entity.Feedback;
import com.example.Entity.Quiz;
import com.example.Entity.User;
import com.example.Enum.FeedbackStatus;
import com.example.Enum.FeedbackType;
import com.example.Exception.ResourceNotFoundException;
import com.example.Repository.FeedbackRepository;
import com.example.Repository.QuizRepository;
import com.example.Repository.UserRepository;
import com.example.Service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    // =============================================
    // Tạo feedback mới
    // =============================================
    @Override
    public FeedbackResponseDTO createFeedback(FeedbackRequestDTO dto) {
        // Validate user tồn tại
        User user = findUserById(dto.getUserId());

        // Validate quiz nếu có
        Quiz quiz = null;
        if (dto.getQuizId() != null) {
            quiz = findQuizById(dto.getQuizId());
        }

        Feedback feedback = new Feedback();
        feedback.setUser_id(dto.getUserId());
        feedback.setQuiz_id(dto.getQuizId());
        feedback.setType(dto.getType());
        feedback.setSubject(dto.getSubject());
        feedback.setMessage(dto.getMessage());
        feedback.setRating(dto.getRating());
        feedback.setStatus(FeedbackStatus.PENDING); // Mặc định PENDING

        Feedback saved = feedbackRepository.save(feedback);
        return mapToDTO(saved, user, quiz);
    }

    // =============================================
    // Cập nhật feedback (chỉ owner, khi PENDING)
    // =============================================
    @Override
    public FeedbackResponseDTO updateFeedback(UUID feedbackId, UUID userId, FeedbackRequestDTO dto) {
        Feedback feedback = findFeedbackById(feedbackId);

        // Chỉ chủ sở hữu mới được sửa
        if (!feedback.getUser_id().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền chỉnh sửa feedback này");
        }
        // Chỉ sửa được khi còn PENDING
        if (feedback.getStatus() != FeedbackStatus.PENDING) {
            throw new IllegalStateException("Chỉ có thể chỉnh sửa feedback đang ở trạng thái PENDING");
        }

        feedback.setType(dto.getType());
        feedback.setSubject(dto.getSubject());
        feedback.setMessage(dto.getMessage());
        feedback.setRating(dto.getRating());

        Feedback updated = feedbackRepository.save(feedback);

        User user = findUserById(updated.getUser_id());
        Quiz quiz = updated.getQuiz_id() != null ? quizRepository.findById(updated.getQuiz_id()).orElse(null) : null;
        return mapToDTO(updated, user, quiz);
    }

    // =============================================
    // Xóa feedback (soft delete)
    // =============================================
    @Override
    public void deleteFeedback(UUID feedbackId, UUID requesterId, boolean isAdmin) {
        Feedback feedback = findFeedbackById(feedbackId);

        if (!isAdmin && !feedback.getUser_id().equals(requesterId)) {
            throw new IllegalStateException("Bạn không có quyền xóa feedback này");
        }

        feedback.delete(); // soft delete từ BaseEntity
        feedbackRepository.save(feedback);
    }

    // =============================================
    // Lấy feedback theo ID
    // =============================================
    @Override
    @Transactional(readOnly = true)
    public FeedbackResponseDTO getFeedbackById(UUID id) {
        Feedback feedback = findFeedbackById(id);
        User user = findUserById(feedback.getUser_id());
        Quiz quiz = feedback.getQuiz_id() != null ? quizRepository.findById(feedback.getQuiz_id()).orElse(null) : null;
        return mapToDTO(feedback, user, quiz);
    }

    // =============================================
    // Lấy feedback theo user
    // =============================================
    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponseDTO> getFeedbackByUser(UUID userId) {
        findUserById(userId); // validate
        return feedbackRepository.findByUser_idOrderByCreatedAtDesc(userId)
                .stream()
                .filter(f -> !Boolean.TRUE.equals(f.getIsDeleted()))
                .map(f -> {
                    User user = findUserById(f.getUser_id());
                    Quiz quiz = f.getQuiz_id() != null
                            ? quizRepository.findById(f.getQuiz_id()).orElse(null)
                            : null;
                    return mapToDTO(f, user, quiz);
                })
                .collect(Collectors.toList());
    }

    // =============================================
    // Lấy feedback theo quiz
    // =============================================
    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponseDTO> getFeedbackByQuiz(UUID quizId) {
        findQuizById(quizId); // validate
        return feedbackRepository.findByQuiz_idOrderByCreatedAtDesc(quizId)
                .stream()
                .filter(f -> !Boolean.TRUE.equals(f.getIsDeleted()))
                .map(f -> {
                    User user = findUserById(f.getUser_id());
                    Quiz quiz = quizRepository.findById(f.getQuiz_id()).orElse(null);
                    return mapToDTO(f, user, quiz);
                })
                .collect(Collectors.toList());
    }

    // =============================================
    // [Admin] Lấy tất cả feedback có filter + phân trang
    // =============================================
    @Override
    @Transactional(readOnly = true)
    public PaginatedData<FeedbackResponseDTO> getAllFeedback(
            FeedbackStatus status, FeedbackType type,
            UUID userId, UUID quizId,
            int page, int limit,
            String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        PageRequest pageable = PageRequest.of(page - 1, limit, sort);

        Page<Feedback> feedbackPage = feedbackRepository
                .findByFilters(status, type, userId, quizId, pageable);

        List<FeedbackResponseDTO> items = feedbackPage.getContent()
                .stream()
                .map(f -> {
                    User user = findUserById(f.getUser_id());
                    Quiz quiz = f.getQuiz_id() != null
                            ? quizRepository.findById(f.getQuiz_id()).orElse(null)
                            : null;
                    return mapToDTO(f, user, quiz);
                })
                .collect(Collectors.toList());

        PaginationMeta meta = PaginationMeta.builder()
                .page(feedbackPage.getNumber() + 1)
                .limit(feedbackPage.getSize())
                .total(feedbackPage.getTotalElements())
                .totalItems(feedbackPage.getTotalElements())
                .totalPages(feedbackPage.getTotalPages())
                .hasNext(feedbackPage.hasNext())
                .hasPrev(feedbackPage.hasPrevious())
                .build();

        return PaginatedData.<FeedbackResponseDTO>builder()
                .items(items)
                .meta(meta)
                .build();
    }

    // =============================================
    // [Admin] Phản hồi & đổi trạng thái feedback
    // =============================================
    @Override
    public FeedbackResponseDTO respondToFeedback(UUID feedbackId, FeedbackAdminResponseDTO dto) {
        Feedback feedback = findFeedbackById(feedbackId);

        feedback.setStatus(dto.getStatus());
        if (dto.getAdminResponse() != null) {
            feedback.setAdmin_response(dto.getAdminResponse());
        }

        Feedback saved = feedbackRepository.save(feedback);
        User user = findUserById(saved.getUser_id());
        Quiz quiz = saved.getQuiz_id() != null ? quizRepository.findById(saved.getQuiz_id()).orElse(null) : null;
        return mapToDTO(saved, user, quiz);
    }

    // =============================================
    // Helper methods
    // =============================================
    private Feedback findFeedbackById(UUID id) {
        return feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", id));
    }

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    private Quiz findQuizById(UUID quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));
    }

    private FeedbackResponseDTO mapToDTO(Feedback feedback, User user, Quiz quiz) {
        return FeedbackResponseDTO.builder()
                .id(feedback.getId())
                .type(feedback.getType())
                .subject(feedback.getSubject())
                .message(feedback.getMessage())
                .rating(feedback.getRating())
                .status(feedback.getStatus())
                .adminResponse(feedback.getAdmin_response())
                .createdAt(feedback.getCreatedAt())
                .updatedAt(feedback.getUpdatedAt())
                .user(UserSummary.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .fullName(user.getFull_name())
                        .avatar(user.getAvatar())
                        .build())
                .quiz(quiz != null
                        ? QuizSummary.builder()
                                .id(quiz.getId())
                                .title(quiz.getTitle())
                                .build()
                        : null)
                .build();
    }
}
