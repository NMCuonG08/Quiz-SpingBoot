package com.example.Entity;

import java.util.UUID;

import com.example.Enum.EventType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "realtimeevent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealTimeEvent extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type")
    private EventType event_type;

    @Column(name = "session_id")
    private UUID session_id;

    @Column(name = "user_id")
    private UUID user_id;

    @Column(name = "event_data", columnDefinition = "TEXT")
    private String event_data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private QuizSession session;

}
