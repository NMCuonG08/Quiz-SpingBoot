package com.example.Entity;

import java.util.UUID;

import com.example.Enum.RoomStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quizroom")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizRoom extends BaseEntity {

    @Column(name = "quiz_id")
    private UUID quiz_id;

    @Column(name = "owner_id")
    private UUID owner_id;

    @Column(name = "room_code", unique = true)
    private String room_code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RoomStatus status;

    @Column(name = "is_private")
    private Boolean is_private;

    @Column(name = "password_hash")
    private String password_hash;

    @Column(name = "max_participants")
    private Integer max_participants;

    @Column(name = "current_participants")
    private Integer current_participants;

    @Column(name = "settings", columnDefinition = "TEXT")
    private String settings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User owner;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<RoomParticipant> participants;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<ActiveConnection> connections;

}
