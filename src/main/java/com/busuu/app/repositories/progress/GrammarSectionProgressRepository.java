package com.busuu.app.repositories.progress;

import com.busuu.app.entities.enums.SectionLevel;
import com.busuu.app.entities.progresses.GrammarSectionProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrammarSectionProgressRepository extends JpaRepository<GrammarSectionProgress, String> {

    GrammarSectionProgress findByGrammarSectionIdAndUserId(String grammarSectionId, String userId);

    List<GrammarSectionProgress> findByLevelAndUserId(SectionLevel level, String userId);

    long countByLevelAndUserId(SectionLevel level, String userId);

    long countByUserId(String userId);
}
