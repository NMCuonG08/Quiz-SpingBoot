package com.example.Entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "quiz_cache")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizCache extends BaseEntity {

    @Column(name = "cache_key", unique = true)
    private String cache_key;

    @Column(name = "quiz_id")
    private UUID quiz_id;

    @Column(name = "data", columnDefinition = "TEXT")
    private String data;

    @Column(name = "expires_at")
    private LocalDateTime expires_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    @JsonIgnore
    private Quiz quiz;

}
