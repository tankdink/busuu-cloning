package com.busuu.app.entities.questions.matching;

import com.busuu.app.entities.questions.Question;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "question_matching")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "question_id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@OnDelete(action = OnDeleteAction.CASCADE)
public class QuestionMatching extends Question {
    @OneToMany(mappedBy = "questionMatching", cascade = CascadeType.ALL)
    private List<MatchingPair> pairs;
}
