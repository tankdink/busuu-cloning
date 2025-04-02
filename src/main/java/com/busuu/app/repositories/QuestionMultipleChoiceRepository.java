package com.busuu.app.repositories;

import com.busuu.app.entities.questions.multiple_choice.QuestionMultipleChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionMultipleChoiceRepository extends JpaRepository<QuestionMultipleChoice, String> {
}
