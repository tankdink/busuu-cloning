package com.busuu.app.repositories.progress;

import com.busuu.app.entities.progresses.GrammarProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrammarProgressRepository extends JpaRepository<GrammarProgress, String> {

    GrammarProgress findGrammarProgressByIdAndUserId(String progressId, String userId);
}
