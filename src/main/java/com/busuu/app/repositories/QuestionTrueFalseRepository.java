package com.busuu.app.repositories;

import com.busuu.app.entities.questions.QuestionTrueFalse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionTrueFalseRepository extends JpaRepository<QuestionTrueFalse, String> {
}
