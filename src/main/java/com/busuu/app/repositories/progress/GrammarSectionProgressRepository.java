package com.busuu.app.repositories.progress;

import com.busuu.app.entities.progresses.GrammarSectionProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrammarSectionProgressRepository extends JpaRepository<GrammarSectionProgress, String> {

    GrammarSectionProgress findByGrammarSectionIdAndUserId(String grammarSectionId, String userId);
}
