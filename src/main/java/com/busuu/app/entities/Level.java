package com.busuu.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "level")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Level {

    @Id
    @Column(name = "level_id")
    private String id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters = new ArrayList<>();

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseLevel> courseLevels = new ArrayList<>();

    @Transient
    private Double progress;

    public void calculateProgress() {
        double totalProgress = chapters.stream().mapToDouble(Chapter::getProgress).sum();
        this.progress = chapters.size() > 0 ? totalProgress / chapters.size() : 0;
    }
}
