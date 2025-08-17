package com.busuu.app.repositories;

import com.busuu.app.entities.Correction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrectionRepository extends JpaRepository<Correction, String>, JpaSpecificationExecutor<Correction>
{
    List<Correction> findByUserId(String userId, Sort sort);
    List<Correction> findByPostId(String postId, Sort sort);


}
