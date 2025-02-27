package com.busuu.app.entities.questions.matching;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "matching_pair")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchingPair {

    @Id
    @Column(name = "matching_pair_id")
    private String id;

    @Column(name = "part_text", nullable = false)
    private String partText;

    @Column(name = "pair_key", nullable = false)
    private String pairKey;

    @Column(name = "part_order")
    private Integer partOrder;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionMatching questionMatching;
}
