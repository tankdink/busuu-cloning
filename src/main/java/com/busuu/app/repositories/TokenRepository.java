package com.busuu.app.repositories;

import com.busuu.app.entities.Token;
import com.busuu.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findByUser (User user);

    Token findByToken (String token);

    Token findByRefreshToken (String token);
}
