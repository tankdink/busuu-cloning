package com.busuu.app.entities.progresses;

import com.busuu.app.entities.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grammar_section_progress")
@Data
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

    @Column(name = "is_completed")
    private Boolean isCompleted;
}
