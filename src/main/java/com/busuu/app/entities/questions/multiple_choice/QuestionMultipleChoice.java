package com.busuu.app.entities.questions.multiple_choice;

import com.busuu.app.entities.questions.Question;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "question_multiple_choice")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "quest_id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@OnDelete(action = OnDeleteAction.CASCADE)
public class QuestionMultipleChoice extends Question {

    @OneToMany(mappedBy = "questionMultipleChoice", cascade = CascadeType.ALL)
    private List<MultipleChoiceOption> options;
}
