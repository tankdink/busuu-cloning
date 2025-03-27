package com.busuu.app.repositories;

import com.busuu.app.entities.GrammarSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrammarSectionRepository extends JpaRepository<GrammarSection, String> {
}
