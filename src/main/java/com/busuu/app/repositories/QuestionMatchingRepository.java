package com.busuu.app.repositories;

import com.busuu.app.entities.questions.matching.QuestionMatching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionMatchingRepository extends JpaRepository<QuestionMatching, String> {
}
