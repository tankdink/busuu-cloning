package com.busuu.app.repositories.questions.ordering;

import com.busuu.app.entities.questions.ordering.OrderingPart;
import com.busuu.app.entities.questions.ordering.QuestionOrdering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderingPartRepository extends JpaRepository<OrderingPart, String> {

    List<OrderingPart> findByQuestionOrdering (QuestionOrdering questionOrdering);

    void deleteByQuestionOrdering (QuestionOrdering questionOrdering);
}
