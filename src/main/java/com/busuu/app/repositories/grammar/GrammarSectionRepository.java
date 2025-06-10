package com.busuu.app.repositories.grammar;

import com.busuu.app.entities.GrammarSection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GrammarSectionRepository extends JpaRepository<GrammarSection, String>
{
    boolean existsByGrammarSectionOrderAndGrammarId(Integer grammarSectionOrder, String grammarId);

    @Query(value = "SELECT gs.* " +
            "FROM grammar_section gs " +
            "JOIN level lv ON gs.level_id = lv.level_id " +
            "JOIN grammar g ON gs.grammar_id = g.grammar_id " +
            "ORDER BY g.title, lv.code, grammar_section_order , lesson_id, grammar_section_id",
            countQuery = """
                    SELECT count(gs.grammar_section_id) 
                    FROM grammar_section gs 
                    """, nativeQuery = true)
    Page<GrammarSection> findAll(Pageable pageable);

    List<GrammarSection> findByGrammarId(String grammarId);

    boolean existsByTitle(String title);

    boolean existsGrammarSectionByGrammarId(String grammarId);

    @Query(value = "SELECT MAX(grammar_section_order) FROM grammar_section WHERE grammar_id = :grammarId", nativeQuery = true)
    Integer findMaxGrammarSectionOrderByGrammarId(@Param("grammarId") String grammarId);
}
