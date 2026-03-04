package com.example.Repository;

import com.example.Entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import com.example.Enum.DifficultyLevel;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, UUID> {
    Page<Quiz> findByCreatorId(UUID creatorId, Pageable pageable);

    @Query("SELECT q FROM Quiz q WHERE q.isDeleted = false AND q.is_public = true AND q.is_active = true")
    Page<Quiz> findPublicQuizzes(Pageable pageable);

    @Query("SELECT q FROM Quiz q WHERE q.isDeleted = false AND q.is_public = true AND q.is_active = true AND q.difficulty_level = :level")
    Page<Quiz> findPublicQuizzesByDifficulty(@Param("level") DifficultyLevel level, Pageable pageable);
}
