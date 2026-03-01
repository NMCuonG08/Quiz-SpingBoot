package com.example.Entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOption extends BaseEntity {

    @Column(name = "question_id")
    private UUID question_id;

    @Column(name = "option_text")
    private String option_text;

    @Column(name = "is_correct")
    private Boolean is_correct;

    @Column(name = "sort_order")
    private Integer sort_order;

    @Column(name = "explanation")
    private String explanation;

    @Column(name = "media_url")
    private String media_url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    @JsonIgnore
    private Question question;

}
