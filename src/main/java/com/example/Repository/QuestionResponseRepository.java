package com.example.Repository;

import com.example.Entity.QuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionResponseRepository extends JpaRepository<QuestionResponse, UUID> {
    Optional<QuestionResponse> findByAttempt_idAndQuestion_id(UUID attemptId, UUID questionId);
}
