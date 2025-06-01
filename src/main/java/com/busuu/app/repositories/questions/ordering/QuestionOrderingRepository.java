package com.busuu.app.repositories.questions.ordering;

import com.busuu.app.entities.questions.ordering.QuestionOrdering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionOrderingRepository extends JpaRepository<QuestionOrdering, String> {
}
