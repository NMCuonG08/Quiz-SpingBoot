package com.example.Entity;

import java.util.UUID;

import com.example.Enum.DifficultyLevel;
import com.example.Enum.QuizType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "quiz")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Quiz extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Column(name = "slug")
    private String slug;

    @Column(name = "description")
    private String description;

    @Column(name = "category_id")
    private UUID category_id;

    @Column(name = "creator_id")
    private UUID creator_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    @JsonIgnore
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", insertable = false, updatable = false)
    @JsonIgnore
    private User creator;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficulty_level;

    @Column(name = "time_limit")
    private Integer time_limit;

    @Column(name = "max_attempts")
    private Integer max_attempts;

    @Column(name = "passing_score")
    private Double passing_score;

    @Column(name = "is_public")
    private Boolean is_public;

    @Column(name = "is_active")
    private Boolean is_active;

    @Enumerated(EnumType.STRING)
    @Column(name = "quiz_type")
    private QuizType quiz_type;

    @Column(name = "instructions")
    private String instructions;

    @Column(name = "thumbnail_id")
    private UUID thumbnail_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thumbnail_id", insertable = false, updatable = false)
    @JsonIgnore
    private Image thumbnail;

    @Column(name = "tags")
    private List<String> tags;

    @Column(name = "settings", columnDefinition = "TEXT")
    private String settings;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Question> questions;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuizAttempt> attempts;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuizSession> sessions;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuizRoom> rooms;

    @OneToOne(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private QuizStatistics statistics;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuizCache> cache;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Leaderboard> leaderboards;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Feedback> feedback;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuizRating> ratings;

}
