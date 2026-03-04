package com.example.Repository;

import com.example.Entity.QuizRoom;
import com.example.Enum.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuizRoomRepository extends JpaRepository<QuizRoom, UUID> {

    @Query("SELECT qr FROM QuizRoom qr WHERE qr.room_code = :roomCode")
    Optional<QuizRoom> findByRoom_code(@Param("roomCode") String roomCode);

    @Query("SELECT qr FROM QuizRoom qr JOIN qr.quiz q WHERE q.id = :quizId")
    org.springframework.data.domain.Page<QuizRoom> findByQuizId(@Param("quizId") java.util.UUID quizId,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT qr FROM QuizRoom qr JOIN qr.quiz q WHERE q.slug = :slug")
    org.springframework.data.domain.Page<QuizRoom> findByQuizSlug(@Param("slug") String slug,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT qr FROM QuizRoom qr WHERE qr.status = :status")
    org.springframework.data.domain.Page<QuizRoom> findByStatus(@Param("status") RoomStatus status,
            org.springframework.data.domain.Pageable pageable);

    @Query("SELECT qr FROM QuizRoom qr JOIN qr.quiz q WHERE q.id = :quizId AND qr.status = :status")
    org.springframework.data.domain.Page<QuizRoom> findByQuizIdAndStatus(@Param("quizId") java.util.UUID quizId,
            @Param("status") RoomStatus status, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT qr FROM QuizRoom qr JOIN qr.quiz q WHERE q.slug = :slug AND qr.status = :status")
    org.springframework.data.domain.Page<QuizRoom> findByQuizSlugAndStatus(@Param("slug") String slug,
            @Param("status") RoomStatus status, org.springframework.data.domain.Pageable pageable);
}
