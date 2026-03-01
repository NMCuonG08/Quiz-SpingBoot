package com.example.Entity;

import java.util.UUID;

import com.example.Enum.SessionStatus;
import com.example.Enum.SessionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "quizsession")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizSession extends BaseEntity {

    @Column(name = "quiz_id")
    private UUID quiz_id;

    @Column(name = "session_code", unique = true)
    private String session_code;

    @Column(name = "host_id")
    private UUID host_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_type")
    private SessionType session_type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private SessionStatus status;

    @Column(name = "max_participants")
    private Integer max_participants;

    @Column(name = "current_participants")
    private Integer current_participants;

    @Column(name = "start_time")
    private LocalDateTime start_time;

    @Column(name = "end_time")
    private LocalDateTime end_time;

    @Column(name = "settings", columnDefinition = "TEXT")
    private String settings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User host;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<SessionParticipant> participants;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<QuizAttempt> attempts;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<ActiveConnection> connections;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<RealTimeEvent> realTimeEvents;

}
