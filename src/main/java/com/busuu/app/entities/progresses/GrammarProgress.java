package com.busuu.app.entities.progresses;

import com.busuu.app.entities.BaseEntity;
import com.busuu.app.entities.Grammar;
import com.busuu.app.entities.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "grammar_progress")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GrammarProgress extends BaseEntity {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "grammar_id", nullable = false)
    private Grammar grammar;

    @Column(name = "progress")
    private Double progress;

    @Column(name = "is_completed")
    private Boolean isCompleted;
}
