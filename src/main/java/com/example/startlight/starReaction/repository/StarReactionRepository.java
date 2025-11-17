package com.example.startlight.starReaction.repository;

import com.example.startlight.starReaction.entity.ReactionType;
import com.example.startlight.starReaction.entity.StarReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StarReactionRepository extends JpaRepository<StarReaction, Long> {
    // 한 글에서 한 유저의 특정 타입 좋아요 한 개
//    Optional<StarReaction> findByMemoryStar_Memory_idAndMember_Member_idAndReactionType(
//            Long memoryStarId,
//            Long userId,
//            ReactionType reactionType
//    );

    @Query("select s from StarReaction s where s.memoryStar.memory_id = :memoryStarId and s.member.member_id = :userId")
    List<StarReaction> findByMemoryIdAndMemberId(@Param("memoryStarId") Long memoryStarId, @Param("userId") Long userId);
}
