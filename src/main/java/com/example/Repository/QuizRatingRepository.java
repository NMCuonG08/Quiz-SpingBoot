package com.example.Repository;

import com.example.Entity.QuizRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizRatingRepository extends JpaRepository<QuizRating, UUID> {

    @Query("SELECT qr FROM QuizRating qr WHERE qr.quiz_id = :quizId")
    Page<QuizRating> findByQuiz_id(@Param("quizId") UUID quizId, Pageable pageable);

    @Query("SELECT qr FROM QuizRating qr WHERE qr.quiz_id = :quizId AND qr.user_id = :userId")
    Optional<QuizRating> findByQuiz_idAndUser_id(@Param("quizId") UUID quizId, @Param("userId") UUID userId);

    @Modifying
    @Query("DELETE FROM QuizRating qr WHERE qr.quiz_id = :quizId AND qr.user_id = :userId")
    void deleteByQuiz_idAndUser_id(@Param("quizId") UUID quizId, @Param("userId") UUID userId);
}
