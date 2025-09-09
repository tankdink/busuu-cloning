package com.busuu.app.entities;

import com.busuu.app.entities.enums.StrengthLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "user_word_history")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserWordHistory extends BaseEntity {

    @Id
    @Column(name = "user_word_history_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_word_id", nullable = false)
    private UserWord userWord;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_strength_level")
    private StrengthLevel previousStrengthLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_strength_level")
    private StrengthLevel newStrengthLevel;

    @Column(name = "result")
    private String result;

    @Column(name = " reviewed_at")
    private Date reviewedAt;
}
