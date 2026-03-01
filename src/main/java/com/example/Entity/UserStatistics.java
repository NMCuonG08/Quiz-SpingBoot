package com.example.Entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "user_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatistics extends BaseEntity {

    @Column(name = "user_id", unique = true)
    private UUID user_id;

    @Column(name = "total_quizzes_taken")
    private Integer total_quizzes_taken;

    @Column(name = "total_quizzes_created")
    private Integer total_quizzes_created;

    @Column(name = "average_score")
    private Double average_score;

    @Column(name = "total_time_spent")
    private Integer total_time_spent;

    @Column(name = "streak_days")
    private Integer streak_days;

    @Column(name = "last_activity_at")
    private LocalDateTime last_activity_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

}
