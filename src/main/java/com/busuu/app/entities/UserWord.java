package com.busuu.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "user_word")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserWord extends BaseEntity {

    @Id
    @Column(name = "user_word_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "word_id", nullable = false)
    private Word word;

    @Enumerated(EnumType.STRING)
    @Column(name = "strength_level")
    @Builder.Default
    private StrengthLevel strengthLevel = StrengthLevel.WEAK;

    @Column(name = "next_review_date")
    private LocalDateTime nextReviewDate;

    @Column(name = "last_reviewed_date")
    private LocalDateTime lastReviewedDate;

    @Column(name = "review_interval")
    @Builder.Default
    private Integer reviewInterval = 1;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    @Column(name = "correct_count")
    @Builder.Default
    private Integer correctCount = 0;

    @Column(name = "incorrect_count")
    @Builder.Default
    private Integer incorrectCount = 0;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_favorite")
    private Boolean isFavorite;
}
