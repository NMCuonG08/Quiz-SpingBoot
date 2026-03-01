package com.example.Entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "question_responses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse extends BaseEntity {

    @Column(name = "attempt_id")
    private UUID attempt_id;

    @Column(name = "question_id")
    private UUID question_id;

    @Column(name = "selected_options")
    private List<String> selected_options;

    @Column(name = "text_answer")
    private String text_answer;

    @Column(name = "is_correct")
    private Boolean is_correct;

    @Column(name = "points_earned")
    private Double points_earned;

    @Column(name = "time_taken")
    private Integer time_taken;

    @Column(name = "answered_at")
    private LocalDateTime answered_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", insertable = false, updatable = false)
    @JsonIgnore
    private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    @JsonIgnore
    private Question question;

}
