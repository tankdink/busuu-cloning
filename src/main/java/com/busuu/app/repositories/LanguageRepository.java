package com.busuu.app.repositories;

import com.busuu.app.dtos.responses.LanguageResponse;
import com.busuu.app.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language, String>
{
    boolean existsByName(String name);


    @Query("""
    SELECT new com.busuu.app.dtos.responses.LanguageResponse(
        l.id, l.name, l.code, l.flagIconUrl, l.flagIconName, COUNT(ul.id)
    )
    FROM Language l
    LEFT JOIN UserLanguage ul
        ON ul.language = l AND ul.learningStatus <> com.busuu.app.entities.enums.LearningStatus.NOT_STARTED
    GROUP BY l.id, l.name, l.code, l.flagIconUrl, l.flagIconName
""")
    List<LanguageResponse> findAllWithTotalUsersLearning();

}
