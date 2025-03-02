package com.busuu.app.entities.questions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "question_true_false")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "question_id")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionTrueFalse extends Question{
    @Column(name = "correct_answer")
    private Boolean correctAnswer;
}
