package com.busuu.app.entities.questions.ordering;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ordering_part")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderingPart {

    @Id
    @Column(name = "ordering_part_id")
    private String id;

    @Column(name = "sentence_part", nullable = false)
    private String sentencePart;

//    @Column(name = "correct_order", nullable = false)
//    private Integer correctOrder;

    @Column(name = "part_order")
    private Integer partOrder;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionOrdering questionOrdering;
}
