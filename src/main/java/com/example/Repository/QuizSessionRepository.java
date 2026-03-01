package com.example.Repository;

import com.example.Entity.QuizSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizSessionRepository extends JpaRepository<QuizSession, UUID> {
    List<QuizSession> findByHostIdAndIsDeletedFalse(UUID hostId);

    List<QuizSession> findByQuizIdAndIsDeletedFalse(UUID quizId);
}
