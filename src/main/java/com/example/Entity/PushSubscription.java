package com.example.Entity;

import java.util.UUID;

import com.example.Enum.DeviceType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "push_subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PushSubscription extends BaseEntity {

    @Column(name = "user_id")
    private UUID user_id;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "p256dh_key")
    private String p256dh_key;

    @Column(name = "auth_key")
    private String auth_key;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type")
    private DeviceType device_type;

    @Column(name = "is_active")
    private Boolean is_active;

    @Column(name = "subscribed_at")
    private LocalDateTime subscribed_at;

    @Column(name = "last_used_at")
    private LocalDateTime last_used_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

}
