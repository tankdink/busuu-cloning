package com.busuu.app.repositories.grammar;

import com.busuu.app.entities.Grammar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GrammarRepository extends JpaRepository<Grammar, String>
{
    boolean existsByGrammarOrderAndLanguageId(Integer grammarOrder, String languageId);
    List<Grammar> findByLanguageId(String languageId);
    boolean existsByTitle(String title);

    @Query(value = "SELECT MAX(grammar_order) FROM grammar WHERE language_id = :languageId", nativeQuery = true)
    Integer findMaxGrammarOrderByLanguageId(String languageId);
}
