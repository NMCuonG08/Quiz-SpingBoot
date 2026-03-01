package com.example.Entity;

import java.util.UUID;

import com.example.Enum.FeedbackStatus;
import com.example.Enum.FeedbackType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback extends BaseEntity {

    @Column(name = "user_id")
    private UUID user_id;

    @Column(name = "quiz_id")
    private UUID quiz_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FeedbackType type;

    @Column(name = "subject")
    private String subject;

    @Column(name = "message")
    private String message;

    @Column(name = "rating")
    private Integer rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FeedbackStatus status;

    @Column(name = "admin_response")
    private String admin_response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    @JsonIgnore
    private Quiz quiz;

}
