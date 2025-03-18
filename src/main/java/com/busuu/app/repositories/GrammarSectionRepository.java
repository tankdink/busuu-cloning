package com.busuu.app.repositories;

import com.busuu.app.entities.GrammarSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GrammarSectionRepository extends JpaRepository<GrammarSection, String>
{
    boolean existsByGrammarSectionOrderAndGrammarId(Integer grammarSectionOrder, String grammarId);
    List<GrammarSection> findByGrammarId(String grammarId);

    boolean existsByTitle(String title);

}
