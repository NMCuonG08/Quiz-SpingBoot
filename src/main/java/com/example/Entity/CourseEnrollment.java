package com.example.Entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "course_enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseEnrollment extends BaseEntity {

    @Column(name = "course_id")
    private UUID course_id;

    @Column(name = "user_id")
    private UUID user_id;

    @Column(name = "progress")
    private Double progress;

    @Column(name = "enrolled_at")
    private LocalDateTime enrolled_at;

    @Column(name = "last_accessed")
    private LocalDateTime last_accessed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    @JsonIgnore
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

}
