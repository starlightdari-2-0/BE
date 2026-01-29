package com.example.startlight.memory.memoryStar.repository;

import com.example.startlight.constellation.entity.AnimalCategory;
import com.example.startlight.memory.memoryStar.entity.MemoryStar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemoryStarRepository extends JpaRepository<MemoryStar, Long> {

    @Query("select m from MemoryStar m where m.writer_id = :userId order by m.memory_id desc")
    List<MemoryStar> findAllByWriterId(@Param("userId") Long userId);

    @Query("SELECT m.memory_id FROM MemoryStar m WHERE m.pet_id = :petId AND m.star_node_id = :starNodeId ")
    Long findByPetIdAndStarNodeId(@Param("petId") Long petId, @Param("starNodeId") Long starNodeId);

    @Query("SELECT count(m) FROM MemoryStar m WHERE m.pet_id = :petId")
    Integer countMemoryStarByPetId(@Param("petId") Long petId);

    @Query("SELECT m FROM MemoryStar m WHERE m.pet_id = :petId")
    List<MemoryStar> findMemoryStarByPet_id(@Param("petId") Long petId);

    @Query("""
        select m
        from MemoryStar m
        where m.shared = true
          and (:category is null or m.pet_id in (
                select p.pet_id from Pet p where p.animal_category =:category
          ))
        order by m.updatedAt desc
    """)
    Page<MemoryStar> findByIsPublicTrueOrderByCreatedAtDesc(@Param("category") AnimalCategory category, Pageable pageable);
}
