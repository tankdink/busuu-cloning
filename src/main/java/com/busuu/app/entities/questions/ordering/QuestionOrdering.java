package com.busuu.app.entities.questions.ordering;

import com.busuu.app.entities.questions.Question;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "question_ordering")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "question_id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@OnDelete(action = OnDeleteAction.CASCADE)
public class QuestionOrdering extends Question {

    @Column(name = "correct_answer")
    private String correctAnswer;

    @OneToMany(mappedBy = "questionOrdering", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderingPart> parts;
}


