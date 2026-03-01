package com.example.Entity;

import com.example.Enum.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notificationtemplate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate extends BaseEntity {

    @Column(name = "name", unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private NotificationType type;

    @Column(name = "subject_template")
    private String subject_template;

    @Column(name = "message_template")
    private String message_template;

    @Column(name = "data_schema", columnDefinition = "TEXT")
    private String data_schema;

}
