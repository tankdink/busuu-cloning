package com.busuu.app.repositories;

import com.busuu.app.entities.reactions.Reaction;
import com.busuu.app.entities.reactions.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReactionRepository extends JpaRepository<Reaction, String>
{
    Reaction findByUserIdAndCorrectionId(String userId, String correctionId);
    boolean existsByUserIdAndCorrectionId(String userId, String correctionId);
    long countByReactionTypeAndCorrectionId(ReactionType reactionType, String correctionId);

}
