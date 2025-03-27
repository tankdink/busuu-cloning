package com.busuu.app.repositories;

import com.busuu.app.entities.Grammar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrammarRepository extends JpaRepository<Grammar, String>
{
    boolean existsByGrammarOrderAndLanguageId(Integer grammarOrder, String languageId);
    List<Grammar> findByLanguageId(String languageId);
    boolean existsByTitle(String title);

}
