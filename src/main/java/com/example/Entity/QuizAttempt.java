package com.example.Entity;

import java.util.UUID;

import com.example.Enum.AttemptStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "quiz_attempts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttempt extends BaseEntity {

    @Column(name = "quiz_id")
    private UUID quiz_id;

    @Column(name = "user_id")
    private UUID user_id;

    @Column(name = "session_id")
    private UUID session_id;

    @Column(name = "attempt_number")
    private Integer attempt_number;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AttemptStatus status;

    @Column(name = "score")
    private Double score;

    @Column(name = "max_score")
    private Double max_score;

    @Column(name = "percentage")
    private Double percentage;

    @Column(name = "passed")
    private Boolean passed;

    @Column(name = "time_taken")
    private Integer time_taken;

    @Column(name = "started_at")
    private LocalDateTime started_at;

    @Column(name = "completed_at")
    private LocalDateTime completed_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    @JsonIgnore
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    @JsonIgnore
    private QuizSession session;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuestionResponse> responses;

}
