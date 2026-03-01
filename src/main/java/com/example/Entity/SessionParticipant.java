package com.example.Entity;

import java.util.UUID;

import com.example.Enum.ParticipantStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "sessionparticipant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SessionParticipant extends BaseEntity {

    @Column(name = "session_id")
    private UUID session_id;

    @Column(name = "user_id")
    private UUID user_id;

    @Column(name = "joined_at")
    private LocalDateTime joined_at;

    @Column(name = "left_at")
    private LocalDateTime left_at;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ParticipantStatus status;

    @Column(name = "device_info", columnDefinition = "TEXT")
    private String device_info;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private QuizSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

}
