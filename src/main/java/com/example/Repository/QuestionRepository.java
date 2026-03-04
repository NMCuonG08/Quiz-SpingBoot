package com.example.Repository;

import com.example.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
        @org.springframework.data.jpa.repository.Query("SELECT q FROM Question q WHERE q.quiz_id = :quizId AND q.isDeleted = false")
        org.springframework.data.domain.Page<Question> findByQuizId(
                        @org.springframework.data.repository.query.Param("quizId") UUID quizId,
                        org.springframework.data.domain.Pageable pageable);

        @org.springframework.data.jpa.repository.Query("SELECT q FROM Question q JOIN q.quiz quiz WHERE quiz.slug = :slug AND q.isDeleted = false")
        org.springframework.data.domain.Page<Question> findByQuizSlug(
                        @org.springframework.data.repository.query.Param("slug") String slug,
                        org.springframework.data.domain.Pageable pageable);

        @org.springframework.data.jpa.repository.Query("SELECT q FROM Question q JOIN q.quiz quiz WHERE quiz.slug = :slug AND q.isDeleted = false")
        java.util.List<Question> findByQuizSlugPublic(
                        @org.springframework.data.repository.query.Param("slug") String slug);
}
