package com.busuu.app.repositories;

import com.busuu.app.entities.WordExample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WordExampleRepository extends JpaRepository<WordExample, String> {

    WordExample findByWordId (String wordId);
}
