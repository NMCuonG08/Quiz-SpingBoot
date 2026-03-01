package com.example.Entity;

import com.example.Enum.LogLevel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "systemlog")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    private LogLevel level;

    @Column(name = "message")
    private String message;

    @Column(name = "context", columnDefinition = "TEXT")
    private String context;

}
