package com.example.startlight.memory.memComment.repository;

import com.example.startlight.memory.memComment.entity.MemComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemCommentRepository extends JpaRepository<MemComment, Long> {

    @Query("SELECT mc from MemComment mc where mc.memoryStar.memory_id = :memoryId ORDER BY mc.comment_id DESC")
    List<MemComment> findAllByMemoryIdDesc(@Param("memoryId") Long memory_id);

    @Query(
            value = "select mc " +
                    "from MemComment mc " +
                    "where mc.memoryStar.memory_id = :memoryId " +
                    "and mc.parent is null",
            countQuery = "select count(mc) " +
                    "from MemComment mc " +
                    "where mc.memoryStar.memory_id = :memoryId " +
                    "and mc.parent is null"
    )
    Page<MemComment> findParentCommentByMemoryId(
            @Param("memoryId") Long memoryId,
            Pageable pageable
    );

    @Query("select mc from MemComment mc where mc.parent.comment_id = :commentId")
    List<MemComment> findChildrenCommentByCommentId(@Param("commentId") Long commentId);

    @Query("select count(mc) from MemComment mc where mc.parent.comment_id = :commentId")
    Long countChildrenComment(@Param("commentId") Long commentId);
}
