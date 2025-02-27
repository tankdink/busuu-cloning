package com.busuu.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "course_level")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseLevel {

    @Id
    @Column(name = "course_level_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "level_id", nullable = false)
    private Level level;
}
