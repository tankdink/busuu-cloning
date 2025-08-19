package com.busuu.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Entity
@Table(name = "user_language")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLanguage {

    @Id
    @Column(name = "user_language_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "language_id", nullable = false)
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(name = "learning_status", nullable = false)
    @Builder.Default
    private LearningStatus learningStatus = LearningStatus.NOTE_STARTED;

    @Column(name = "date_started")
    private Date dateStarted;

    @Column(name = "date_completed")
    private Date dateCompleted;
}
