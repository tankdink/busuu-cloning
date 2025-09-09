package com.busuu.app.repositories;

import com.busuu.app.entities.UserLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLanguageRepository extends JpaRepository<UserLanguage, String> {

    List<UserLanguage> findByUserId (String userId);

    UserLanguage findByUserIdAndLanguageId (String userId, String languageId);
}
