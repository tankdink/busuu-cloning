package com.busuu.app.repositories;

import com.busuu.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<String, User> {
    boolean existsByEmail (String email);

    Optional<User> findByEmail (String email);

}
