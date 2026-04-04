package com.example.startlight.community.post.postReaction.repository;

import com.example.startlight.community.post.postReaction.entity.PostReaction;
import com.example.startlight.global.entity.ReactionType;
import com.example.startlight.memory.starReaction.entity.StarReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    @Query("select s from PostReaction s where s.post.post_id = :postId and s.member.member_id = :userId and s.reactionType = :reactionType")
    Optional<PostReaction> findByPostIdAndMemberIdAndReactionType(
            @Param("postId") Long postId,
            @Param("userId") Long userId,
            @Param("reactionType") ReactionType reactionType
    );

    @Query("select s from PostReaction s where s.post.post_id = :postId and s.member.member_id = :userId")
    List<PostReaction> findByPostIdAndMemberId(@Param("postId") Long postId, @Param("userId") Long userId);
}
