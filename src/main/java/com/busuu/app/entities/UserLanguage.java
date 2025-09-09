package com.busuu.app.entities;

import com.busuu.app.entities.enums.LearningStatus;
import com.busuu.app.entities.enums.SpeakingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "user_language")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLanguage extends BaseEntity {

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
    private LearningStatus learningStatus = LearningStatus.NOT_STARTED;

    @Enumerated(EnumType.STRING)
    @Column(name = "speaking_status")
    @Builder.Default
    private SpeakingStatus speakingStatus = null;

    @Column(name = "date_started")
    private Date dateStarted;

    @Column(name = "date_completed")
    private Date dateCompleted;

    @Column(name = "is_learning")
    private Boolean isLearning;
}
