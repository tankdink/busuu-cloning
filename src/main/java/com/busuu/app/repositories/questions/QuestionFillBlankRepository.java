package com.busuu.app.repositories.questions;

import com.busuu.app.entities.questions.QuestionFillBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionFillBlankRepository extends JpaRepository<QuestionFillBlank, String> {
}
