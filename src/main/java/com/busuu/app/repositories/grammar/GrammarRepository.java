package com.busuu.app.repositories.grammar;

import com.busuu.app.entities.Grammar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GrammarRepository extends JpaRepository<Grammar, String>, JpaSpecificationExecutor<Grammar>
{
    boolean existsByGrammarOrderAndLanguageId(Integer grammarOrder, String languageId);
    Page<Grammar> findByLanguageId(String languageId, Pageable pageable);
    boolean existsByTitle(String title);

    @Query(value = "SELECT MAX(grammar_order) FROM grammar WHERE language_id = :languageId", nativeQuery = true)
    Integer findMaxGrammarOrderByLanguageId(String languageId);
}
