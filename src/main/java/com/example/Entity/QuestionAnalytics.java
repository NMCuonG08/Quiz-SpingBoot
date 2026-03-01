package com.example.Entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "question_analytics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnalytics extends BaseEntity {

    @Column(name = "question_id", unique = true)
    private UUID question_id;

    @Column(name = "total_responses")
    private Integer total_responses;

    @Column(name = "correct_responses")
    private Integer correct_responses;

    @Column(name = "incorrect_responses")
    private Integer incorrect_responses;

    @Column(name = "average_time")
    private Double average_time;

    @Column(name = "difficulty_index")
    private Double difficulty_index;

    @Column(name = "discrimination_index")
    private Double discrimination_index;

    @Column(name = "last_calculated_at")
    private LocalDateTime last_calculated_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    @JsonIgnore
    private Question question;

}
