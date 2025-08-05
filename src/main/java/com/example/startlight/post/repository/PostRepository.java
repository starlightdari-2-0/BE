package com.example.startlight.post.repository;

import com.example.startlight.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p ORDER BY p.updatedAt DESC")  // ✅ 최신순 정렬
    List<Post> findAllByOrderByCreatedAtDesc();
}
