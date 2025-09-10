package com.busuu.app.repositories;

import com.busuu.app.entities.enums.StrengthLevel;
import com.busuu.app.entities.UserWord;
import com.busuu.app.entities.Word;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserWordRepository extends JpaRepository<UserWord, String> {

    boolean existsByUserIdAndWordId(String userId, String wordId);

    Optional<UserWord> findByUserIdAndWordId(String userId, String wordId);

    @Query("""
    SELECT uw FROM UserWord uw
    JOIN uw.word w
    WHERE uw.user.id = :userId
      AND uw.isActive = true
      AND (:strengthLevel IS NULL OR uw.strengthLevel = :strengthLevel)
      AND (:isFavorite IS NULL OR uw.isFavorite = :isFavorite)
      AND (:languageCode IS NULL OR :languageCode = '' OR LOWER(w.languageCode) = LOWER(:languageCode))
""")
    List<UserWord> findByUserIdWithFilter(
            @Param("userId") String userId,
            @Param("strengthLevel") StrengthLevel strengthLevel,
            @Param("isFavorite") Boolean isFavorite,
            @Param("languageCode") String languageCode
    );


    @Query("""
        SELECT COUNT(uw) FROM UserWord uw
        WHERE uw.user.id = :userId
        AND uw.isActive = true
        AND (:strengthLevel IS NULL OR uw.strengthLevel = :strengthLevel)
        AND (:isFavorite IS NULL OR uw.isFavorite = :isFavorite)
    """)
    Long countByUserIdWithFilter(
            @Param("userId") String userId,
            @Param("strengthLevel") StrengthLevel strengthLevel,
            @Param("isFavorite") Boolean isFavorite
    );

        @Query("""
        SELECT uw.strengthLevel, uw.isFavorite, COUNT(uw) 
        FROM UserWord uw
        WHERE uw.user.id = :userId
          AND uw.isActive = true
          AND (:languageCode IS NULL OR :languageCode = '' OR uw.word.languageCode = :languageCode)
        GROUP BY uw.strengthLevel, uw.isFavorite
    """)
    List<Object[]> countGroupedByStrengthAndFavorite(
            @Param("userId") String userId,
            @Param("languageCode") String languageCode
    );


    // All
    @Query("""
        SELECT uw.word FROM UserWord uw
        JOIN uw.word w
        WHERE uw.user.id = :userId
          AND uw.isActive = true
          AND uw.nextReviewDate <= CURRENT_TIMESTAMP
          AND (:languageCode IS NULL OR :languageCode = '' OR LOWER(w.languageCode) = LOWER(:languageCode))
        ORDER BY uw.nextReviewDate ASC
    """)
    List<Word> findReviewAll(
            @Param("userId") String userId,
            @Param("languageCode") String languageCode,
            Pageable pageable
    );


    // By Strength
    @Query("""
        SELECT uw.word FROM UserWord uw
        JOIN uw.word w
        WHERE uw.user.id = :userId
          AND uw.isActive = true
          AND uw.strengthLevel = :strengthLevel
          AND uw.nextReviewDate <= CURRENT_TIMESTAMP
          AND (:languageCode IS NULL OR :languageCode = '' OR LOWER(w.languageCode) = LOWER(:languageCode))
        ORDER BY uw.nextReviewDate ASC
    """)
    List<Word> findReviewByStrength(
            @Param("userId") String userId,
            @Param("strengthLevel") StrengthLevel strengthLevel,
            @Param("languageCode") String languageCode,
            Pageable pageable
    );


    // Favorite
    @Query("""
        SELECT uw.word FROM UserWord uw
        JOIN uw.word w
        WHERE uw.user.id = :userId
          AND uw.isActive = true
          AND uw.isFavorite = true
          AND uw.nextReviewDate <= CURRENT_TIMESTAMP
          AND (:languageCode IS NULL OR :languageCode = '' OR LOWER(w.languageCode) = LOWER(:languageCode))
        ORDER BY uw.nextReviewDate ASC
    """)
    List<Word> findReviewFavorite(
            @Param("userId") String userId,
            @Param("languageCode") String languageCode,
            Pageable pageable
    );


}
