package com.busuu.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "user_word")
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    private StrengthLevel strengthLevel = StrengthLevel.WEAK;

    @Column(name = "next_review_date")
    private Date nextReviewDate;

    @Column(name = "interval")
    private Integer interval = 1;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_favorite")
    private Boolean isFavorite;
}
