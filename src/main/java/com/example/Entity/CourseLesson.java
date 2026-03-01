package com.example.Entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_lessons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseLesson extends BaseEntity {

    @Column(name = "module_id")
    private UUID module_id;

    @Column(name = "title")
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "video_url")
    private String video_url;

    @Column(name = "sort_order")
    private Integer sort_order;

    @Column(name = "is_free")
    private Boolean is_free;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", insertable = false, updatable = false)
    @JsonIgnore
    private CourseModule module;

}
