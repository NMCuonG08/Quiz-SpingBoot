package com.example.Entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "quiz_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizStatistics extends BaseEntity {

    @Column(name = "quiz_id", unique = true)
    private UUID quiz_id;

    @Column(name = "total_attempts")
    private Integer total_attempts;

    @Column(name = "total_participants")
    private Integer total_participants;

    @Column(name = "average_score")
    private Double average_score;

    @Column(name = "average_time")
    private Double average_time;

    @Column(name = "pass_rate")
    private Double pass_rate;

    @Column(name = "difficulty_rating")
    private Double difficulty_rating;

    @Column(name = "last_calculated_at")
    private LocalDateTime last_calculated_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    @JsonIgnore
    private Quiz quiz;

}
