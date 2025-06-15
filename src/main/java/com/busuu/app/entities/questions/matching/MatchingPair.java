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

    @Column(name = "pair_text", nullable = false)
    private String pairText;

    @Column(name = "pair_key", nullable = false)
    private String pairKey;

    @Column(name = "pair_order")
    private Integer pairOrder;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionMatching questionMatching;
}
