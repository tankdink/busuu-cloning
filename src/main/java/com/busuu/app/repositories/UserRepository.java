package com.busuu.app.repositories;

import com.busuu.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByEmail (String email);

    Optional<User> findByEmail (String email);

    @Query(value = """
        SELECT u.* 
        FROM user u 
        JOIN user_role ur ON u.user_id = ur.user_id 
        JOIN role r ON ur.role_id = r.role_id 
        WHERE r.name = :roleName
        """, nativeQuery = true)
    List<User> findUsersByRoleName(@Param("roleName") String roleName);
}
