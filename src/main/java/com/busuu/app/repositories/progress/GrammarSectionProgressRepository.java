package com.busuu.app.repositories.progress;

import com.busuu.app.entities.enums.SectionLevel;
import com.busuu.app.entities.progresses.GrammarSectionProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrammarSectionProgressRepository extends JpaRepository<GrammarSectionProgress, String> {

    GrammarSectionProgress findByGrammarSectionIdAndUserId(String grammarSectionId, String userId);

    @Query("""
    SELECT gsp
    FROM GrammarSectionProgress gsp
    JOIN gsp.grammarSection gs
    JOIN gs.grammar g
    JOIN g.language l
    WHERE gsp.level = :level
      AND gsp.user.id = :userId
      AND l.id = :languageId
""")
    List<GrammarSectionProgress> findByLevelAndUserIdAndLanguageId(
            @Param("level") SectionLevel level,
            @Param("userId") String userId,
            @Param("languageId") String languageId
    );


    @Query("""
    SELECT COUNT(gsp)
    FROM GrammarSectionProgress gsp
    JOIN gsp.grammarSection gs
    JOIN gs.grammar g
    JOIN g.language l
    WHERE gsp.level = :level
      AND gsp.user.id = :userId
      AND l.id = :languageId
""")
    long countByLevelAndUserIdAndLanguageId(
            @Param("level") SectionLevel level,
            @Param("userId") String userId,
            @Param("languageId") String languageId
    );

    @Query("""
    SELECT COUNT(gsp)
    FROM GrammarSectionProgress gsp
    JOIN gsp.grammarSection gs
    JOIN gs.grammar g
    JOIN g.language l
    WHERE gsp.user.id = :userId
      AND l.id = :languageId
""")
    long countByUserIdAndLanguageId(
            @Param("userId") String userId,
            @Param("languageId") String languageId
    );

}
