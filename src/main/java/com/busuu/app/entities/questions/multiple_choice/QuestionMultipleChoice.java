package com.busuu.app.entities.questions.multiple_choice;

import com.busuu.app.entities.questions.Question;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "question_multiple_choice")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "quest_id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionMultipleChoice extends Question {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<MultipleChoiceOption> options;
}
