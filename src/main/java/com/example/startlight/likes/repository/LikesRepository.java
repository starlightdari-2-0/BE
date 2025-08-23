package com.example.startlight.likes.repository;

import com.example.startlight.likes.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    @Query("select L from Likes L where L.member_id = :user_id and L.target_id = :target_id")
    public Likes findLikesByMember_idAndTarget_id(Long user_id, Long target_id);
}
