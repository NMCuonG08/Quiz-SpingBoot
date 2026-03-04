package com.example.Repository;

import com.example.Entity.QuizAttempt;
import com.example.Enum.AttemptStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    Optional<QuizAttempt> findFirstByQuiz_idAndUser_idAndStatusOrderByCreatedAtDesc(UUID quizId, UUID userId,
            AttemptStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT qa FROM QuizAttempt qa WHERE qa.user_id = :userId ORDER BY qa.createdAt DESC")
    java.util.List<QuizAttempt> findByUser_idOrderByCreatedAtDesc(
            @org.springframework.data.repository.query.Param("userId") UUID userId);
}
