package com.example.startlight.memComReply.repository;

import com.example.startlight.memComReply.entity.MemComReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MemComReplyRepository extends JpaRepository<MemComReply, Long> {

    @Query("select m from MemComReply m where m.memComment.comment_id = :commentId")
    List<MemComReply> findAllByMemComment_Comment_id(Long commentId);
}
