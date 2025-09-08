package com.busuu.app.entities.progresses;

import com.busuu.app.entities.*;
import com.busuu.app.entities.enums.SectionLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "grammar_section_progress")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrammarSectionProgress extends BaseEntity {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "grammar_section_id", nullable = false)
    private GrammarSection grammarSection;

    @Column(name = "progress")
    private Double progress;

    @Column(name = "level")
    @Enumerated(EnumType.STRING)
    private SectionLevel level;

    @Column(name = "is_completed")
    private Boolean isCompleted;
}
