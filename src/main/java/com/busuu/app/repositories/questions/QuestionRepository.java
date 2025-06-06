package com.busuu.app.repositories.questions;

import com.busuu.app.entities.questions.Question;
import com.busuu.app.entities.questions.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {

    List<Question> findByLessonId (String lessonId);

    List<Question> findByGrammarSectionId (String grammarSectionId);

    Page<Question> findByQuestionType(QuestionType questionType, Pageable pageable);

    void deleteByLessonId (String lessonId);

    void deleteByGrammarSectionId (String grammarSectionId);

    @Query(value = "SELECT MAX(question_order) FROM question WHERE lesson_id = :lessonId", nativeQuery = true)
    Integer findMaxQuestionOrderByLessonId(@Param("lessonId") String lessonId);

    @Query(value = "SELECT MAX(question_order) FROM question WHERE grammar_section_id = :grammarSectionId", nativeQuery = true)
    Integer findMaxQuestionOrderByGrammarSectionId(@Param("grammarSectionId") String grammarSectionId);
}
