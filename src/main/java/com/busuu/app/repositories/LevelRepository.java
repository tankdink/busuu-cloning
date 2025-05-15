package com.busuu.app.repositories;

import com.busuu.app.entities.Chapter;
import com.busuu.app.entities.Level;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository extends JpaRepository<Level, String>
{
    Boolean existsByCode(String code);
    Boolean existsByName(String name);
    Level findByCode (String Code);

}