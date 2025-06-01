package com.busuu.app.entities;

import com.busuu.app.entities.progresses.ChapterProgress;
import jakarta.persistence.*;
import lombok.*;
import org.modelmapper.config.Configuration;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chapter")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chapter extends BaseEntity {

    @Id
    @Column(name = "chapter_id")
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "chapter_order")
    private Integer chapterOrder;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = true)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "level_id", nullable = true)
    private Level level;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChapterProgress> chapterProgresses;
}
