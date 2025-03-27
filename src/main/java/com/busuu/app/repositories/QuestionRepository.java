package com.busuu.app.repositories;

import com.busuu.app.entities.questions.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    List<Question> findByLessonId (String lessonId);

    List<Question> findByGrammarSectionId (String grammarSectionId);

    void deleteByLessonId (String lessonId);

    void deleteByGrammarSectionId (String grammarSectionId);
}
