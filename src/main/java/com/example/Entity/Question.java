package com.example.Entity;

import java.util.UUID;

import com.example.Enum.DifficultyLevel;
import com.example.Enum.MediaType;
import com.example.Enum.QuestionType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Question extends BaseEntity {

    @Column(name = "quiz_id")
    private UUID quiz_id;

    @Column(name = "question_text")
    private String question_text;

    @Column(name = "slug")
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type")
    private QuestionType question_type;

    @Column(name = "points")
    private Double points;

    @Column(name = "time_limit")
    private Integer time_limit;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "media_id")
    private UUID media_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type")
    private MediaType media_type;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level")
    private DifficultyLevel difficulty_level;

    @Column(name = "sort_order")
    private Integer sort_order;

    @Column(name = "is_required")
    private Boolean is_required;

    @Column(name = "settings", columnDefinition = "TEXT")
    private String settings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Image media;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<QuestionOption> options;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<QuestionResponse> responses;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private QuestionAnalytics analytics;

}
