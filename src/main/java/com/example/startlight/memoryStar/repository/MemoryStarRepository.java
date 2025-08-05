package com.example.startlight.memoryStar.repository;

import com.example.startlight.memoryStar.entity.MemoryStar;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemoryStarRepository extends JpaRepository<MemoryStar, Long> {

    @Query("select m from MemoryStar m where m.shared = true order by m.memory_id desc")
    List<MemoryStar> findBySharedTrue();

    @Query("select m from MemoryStar m where m.writer_id = :userId order by m.memory_id desc")
    List<MemoryStar> findAllByWriterId(@Param("userId") Long userId);

    @Query("select count(m) from MemoryStar m where m.pet_id = :petId and m.isAnimal = true")
    Integer countMemoryStarByPetId(@Param("petId") Long petId);

    @Query("select m from MemoryStar m where m.starList.star_id = :starId")
    MemoryStar findByStarListId(@Param("starId") Long starId);

    @Query("select m.img_url from MemoryStar m where m.starList.pet.pet_id = :petId and m.isAnimal = true order by m.updatedAt desc")
    List<String> findPetImagesByPetId(@Param("petId") Long petId, Pageable pageable);

    @Query("SELECT m FROM MemoryStar m WHERE m.usedToGenerate = false AND m.pet_id = :petId ORDER BY m.updatedAt ASC")
    List<MemoryStar> findMemoryStarByPetIdUnused(@Param("petId") Long petId, Pageable pageable);
}
