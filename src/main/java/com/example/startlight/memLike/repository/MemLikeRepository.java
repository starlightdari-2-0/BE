package com.example.startlight.memLike.repository;

import com.example.startlight.memLike.entity.MemLike;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemLikeRepository extends JpaRepository<MemLike, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM MemLike m WHERE m.memoryStar.memory_id = :memoryId AND m.member_id = :memberId")
    void deleteByMemoryAndMember(@Param("memoryId") Long memoryId, @Param("memberId") Long memberId);

    @Query("SELECT COUNT(m) > 0 FROM MemLike m WHERE m.memoryStar.memory_id = :memoryId AND m.member_id = :memberId")
    boolean existsByMemoryAndMember(@Param("memoryId") Long memoryId, @Param("memberId") Long memberId);
}
