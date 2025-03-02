package com.busuu.app.entities.questions.matching;

import com.busuu.app.entities.questions.Question;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "question_matching")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "question_id")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionMatching extends Question {
    @OneToMany(mappedBy = "questionMatching", cascade = CascadeType.ALL)
    private List<MatchingPair> pairs;
}
