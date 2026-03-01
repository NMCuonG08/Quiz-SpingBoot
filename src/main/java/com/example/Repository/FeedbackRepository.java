package com.example.Repository;

import com.example.Entity.Feedback;
import com.example.Enum.FeedbackStatus;
import com.example.Enum.FeedbackType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {

    // Lấy tất cả feedback của 1 user
    List<Feedback> findByUser_idOrderByCreatedAtDesc(UUID userId);

    // Lấy feedback theo quiz
    List<Feedback> findByQuiz_idOrderByCreatedAtDesc(UUID quizId);

    // Lấy theo status
    Page<Feedback> findByStatus(FeedbackStatus status, Pageable pageable);

    // Lấy theo type
    Page<Feedback> findByType(FeedbackType type, Pageable pageable);

    // Lấy tất cả có phân trang
    Page<Feedback> findAll(Pageable pageable);

    // Lọc đa điều kiện
    @Query("SELECT f FROM Feedback f WHERE " +
            "(:status IS NULL OR f.status = :status) AND " +
            "(:type IS NULL OR f.type = :type) AND " +
            "(:userId IS NULL OR f.user_id = :userId) AND " +
            "(:quizId IS NULL OR f.quiz_id = :quizId)")
    Page<Feedback> findByFilters(
            @Param("status") FeedbackStatus status,
            @Param("type") FeedbackType type,
            @Param("userId") UUID userId,
            @Param("quizId") UUID quizId,
            Pageable pageable);

    // Đếm theo status
    long countByStatus(FeedbackStatus status);

    // Kiểm tra user đã feedback quiz chưa
    boolean existsByUser_idAndQuiz_id(UUID userId, UUID quizId);
}
