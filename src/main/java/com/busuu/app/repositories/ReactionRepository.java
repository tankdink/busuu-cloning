package com.busuu.app.repositories;

import com.busuu.app.entities.Reaction;
import com.busuu.app.entities.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, String>
{

    Reaction findByUserIdAndCorrectionId(String userId, String correctionId);

    long countByReactionTypeAndCorrectionId(ReactionType reactionType, String correctionId);

}
