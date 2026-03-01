package com.example.Entity;

import java.util.UUID;

import com.example.Enum.ConnectionType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "activeconnection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActiveConnection extends BaseEntity {

    @Column(name = "user_id")
    private UUID user_id;

    @Column(name = "socket_id", unique = true)
    private UUID socket_id;

    @Column(name = "session_id")
    private UUID session_id;

    @Column(name = "room_id")
    private UUID room_id;

    @Enumerated(EnumType.STRING)
    @Column(name = "connection_type")
    private ConnectionType connection_type;

    @Column(name = "connected_at")
    private LocalDateTime connected_at;

    @Column(name = "last_ping")
    private LocalDateTime last_ping;

    @Column(name = "device_info", columnDefinition = "TEXT")
    private String device_info;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private QuizSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private QuizRoom room;

}
