package com.busuu.app.entities;

import com.busuu.app.entities.questions.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grammar_section")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrammarSection extends BaseEntity {

    @Id
    @Column(name = "grammar_section_id")
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "grammar_section_order")
    private Integer grammarSectionOrder;

    @ManyToOne
    @JoinColumn(name = "grammar_id", nullable = false)
    private Grammar grammar;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @OneToMany(mappedBy = "grammarSection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();
}
