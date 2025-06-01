package com.busuu.app.repositories.questions.mutilple_choice;

import com.busuu.app.entities.questions.multiple_choice.MultipleChoiceOption;
import com.busuu.app.entities.questions.multiple_choice.QuestionMultipleChoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultipleChoiceOptionRepository extends JpaRepository<MultipleChoiceOption, String> {

    List<MultipleChoiceOption> findByQuestionMultipleChoice (QuestionMultipleChoice questionMultipleChoice);

    void deleteByQuestionMultipleChoice (QuestionMultipleChoice questionMultipleChoice);
}
