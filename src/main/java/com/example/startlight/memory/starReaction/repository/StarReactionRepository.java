package com.example.startlight.memory.starReaction.repository;

import com.example.startlight.memory.starReaction.entity.ReactionType;
import com.example.startlight.memory.starReaction.entity.StarReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StarReactionRepository extends JpaRepository<StarReaction, Long> {

    @Query("select s from StarReaction s where s.memoryStar.memory_id = :memoryStarId and s.member.member_id = :userId and s.reactionType = :reactionType")
    Optional<StarReaction> findByMemoryIdAndMemberIdAndReactionType(
            @Param("memoryStarId") Long memoryStarId,
            @Param("userId") Long userId,
            @Param("reactionType") ReactionType reactionType
    );

    @Query("select s from StarReaction s where s.memoryStar.memory_id = :memoryStarId and s.member.member_id = :userId")
    List<StarReaction> findByMemoryIdAndMemberId(@Param("memoryStarId") Long memoryStarId, @Param("userId") Long userId);
}
