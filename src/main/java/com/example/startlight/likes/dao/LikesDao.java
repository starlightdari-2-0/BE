package com.example.startlight.likes.dao;

import com.example.startlight.likes.entity.Likes;
import com.example.startlight.likes.entity.TargetType;
import com.example.startlight.likes.repository.LikesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LikesDao {
    private final LikesRepository likesRepository;

   public Likes save(Likes likes) {
       return likesRepository.save(likes);
   }

   public Likes createReplyLike(Long userId, Long commentId) {
       Likes likes = Likes.builder()
               .target_type(TargetType.MEMORY_COMMENT_LIKE)
               .target_id(commentId)
               .member_id(userId).build();
       return likesRepository.save(likes);
   }

   public void deleteReplyLike(Long userId, Long targetId) {
       Likes likes = likesRepository.findLikesByMember_idAndTarget_id(userId, targetId);
       likesRepository.delete(likes);
   }
}
