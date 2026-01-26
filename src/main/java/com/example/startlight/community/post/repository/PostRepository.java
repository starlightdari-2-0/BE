package com.example.startlight.community.post.repository;

import com.example.startlight.community.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p ORDER BY p.updatedAt DESC")  // ✅ 최신순 정렬
    List<Post> findAllByOrderByCreatedAtDesc();

    @Query("SELECT p FROM Post p WHERE p.member.member_id = :userId")
    List<Post> findAllByMemberId(@Param("userId") Long userId);
}

