package com.busuu.app.repositories;

import com.busuu.app.entities.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, String>
{

}
