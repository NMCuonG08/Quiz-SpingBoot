package com.example.Service;

import com.example.DTO.Request.FeedbackAdminResponseDTO;
import com.example.DTO.Request.FeedbackRequestDTO;
import com.example.DTO.Response.FeedbackResponseDTO;
import com.example.DTO.Response.PaginatedData;
import com.example.Enum.FeedbackStatus;
import com.example.Enum.FeedbackType;

import java.util.List;
import java.util.UUID;

public interface FeedbackService {

    /** Tạo feedback mới (user gửi) */
    FeedbackResponseDTO createFeedback(FeedbackRequestDTO dto);

    /** Cập nhật feedback (chỉ user chủ sở hữu, khi status còn PENDING) */
    FeedbackResponseDTO updateFeedback(UUID feedbackId, UUID userId, FeedbackRequestDTO dto);

    /** Xóa feedback (soft delete) */
    void deleteFeedback(UUID feedbackId, UUID requesterId, boolean isAdmin);

    /** Lấy feedback theo ID */
    FeedbackResponseDTO getFeedbackById(UUID id);

    /** Lấy tất cả feedback của 1 user */
    List<FeedbackResponseDTO> getFeedbackByUser(UUID userId);

    /** Lấy tất cả feedback của 1 quiz */
    List<FeedbackResponseDTO> getFeedbackByQuiz(UUID quizId);

    /** [Admin] Lấy tất cả feedback có lọc + phân trang */
    PaginatedData<FeedbackResponseDTO> getAllFeedback(
            FeedbackStatus status,
            FeedbackType type,
            UUID userId,
            UUID quizId,
            int page, int limit,
            String sortBy, String sortDir);

    /** [Admin] Phản hồi & cập nhật trạng thái feedback */
    FeedbackResponseDTO respondToFeedback(UUID feedbackId, FeedbackAdminResponseDTO dto);
}
