package com.example.startlight.community.postComment.repository;

import com.example.startlight.community.postComment.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    @Query("SELECT pc FROM PostComment pc WHERE pc.post.post_id = :postId ORDER BY pc.createdAt DESC")
    List<PostComment> findAllByPostId(@Param("postId") Long postId);
}
