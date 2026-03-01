package com.example.Entity;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "activitylog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog extends BaseEntity {

    @Column(name = "user_id")
    private UUID user_id;

    @Column(name = "action")
    private String action;

    @Column(name = "resource_type")
    private String resource_type;

    @Column(name = "resource_id")
    private UUID resource_id;

    @Column(name = "ip_address")
    private String ip_address;

    @Column(name = "user_agent")
    private String user_agent;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

}
