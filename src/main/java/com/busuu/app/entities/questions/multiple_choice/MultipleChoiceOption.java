package com.busuu.app.entities.questions.multiple_choice;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "multiple_choice_option")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultipleChoiceOption {

    @Id
    @Column(name = "multiple_choice_option_id")
    private String id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionMultipleChoice question;

    @Column(name = "option_text", nullable = false)
    private String optionText;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "option_order")
    private Integer optionOrder;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionMultipleChoice questionMultipleChoice;
}
