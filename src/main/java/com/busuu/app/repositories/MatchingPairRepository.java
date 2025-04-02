package com.busuu.app.repositories;

import com.busuu.app.entities.questions.matching.MatchingPair;
import com.busuu.app.entities.questions.matching.QuestionMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchingPairRepository extends JpaRepository<MatchingPair, String> {

    List<MatchingPair> findByQuestionMatching (QuestionMatching questionMatching);

    void deleteByQuestionMatching (QuestionMatching questionMatching);
}
