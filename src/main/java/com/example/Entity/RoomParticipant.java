package com.example.Entity;

import java.util.UUID;

import com.example.Enum.ParticipantStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "roomparticipant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomParticipant extends BaseEntity {

    @Column(name = "room_id")
    private UUID room_id;

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
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private QuizRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

}
