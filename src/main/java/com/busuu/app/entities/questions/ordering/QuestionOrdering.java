package com.busuu.app.entities.questions.ordering;

import com.busuu.app.entities.questions.Question;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "question_ordering")
@EqualsAndHashCode(callSuper = true)
@PrimaryKeyJoinColumn(name = "question_id")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionOrdering extends Question {
    @OneToMany(mappedBy = "questionOrdering", cascade = CascadeType.ALL)
    private List<OrderingPart> parts;
}


