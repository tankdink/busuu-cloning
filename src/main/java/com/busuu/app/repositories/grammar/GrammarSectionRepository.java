package com.busuu.app.repositories.grammar;

import com.busuu.app.entities.GrammarSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GrammarSectionRepository extends JpaRepository<GrammarSection, String>
{
    boolean existsByGrammarSectionOrderAndGrammarId(Integer grammarSectionOrder, String grammarId);
    List<GrammarSection> findByGrammarId(String grammarId);

    boolean existsByTitle(String title);

    boolean existsGrammarSectionByGrammarId(String grammarId);

    @Query(value = "SELECT MAX(grammar_section_order) FROM grammar_section WHERE grammar_id = :grammarId", nativeQuery = true)
    Integer findMaxGrammarSectionOrderByGrammarId(@Param("grammarId") String grammarId);
}
