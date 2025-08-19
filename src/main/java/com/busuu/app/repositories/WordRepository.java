package com.busuu.app.repositories;

import com.busuu.app.entities.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordRepository extends JpaRepository<Word, String> {

    Page<Word> findAll(Pageable pageable);

    Page<Word> findByLessonId(String lessonId, Pageable pageable);
}
