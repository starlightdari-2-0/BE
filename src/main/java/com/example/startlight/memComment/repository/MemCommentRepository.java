package com.example.startlight.memComment.repository;

import com.example.startlight.memComment.entity.MemComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemCommentRepository extends JpaRepository<MemComment, Long> {

    @Query("SELECT mc from MemComment mc where mc.memoryStar.memory_id = :memoryId ORDER BY mc.comment_id DESC")
    List<MemComment> findAllByMemoryIdDesc(@Param("memoryId") Long memory_id);
}
