package com.example.startlight.memoryAlbum.repository;

import com.example.startlight.memoryAlbum.entity.MemoryAlbum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemoryAlbumRepository extends JpaRepository<MemoryAlbum, Long> {
    @Query("select ma from MemoryAlbum ma where ma.pet.pet_id = :petId order by ma.createdAt desc")
    List<MemoryAlbum> findAllByPetId(@Param("petId") Long petId);

    @Query("select count(ma) from MemoryAlbum ma where ma.pet.pet_id = :petId and ma.opened = false")
    Integer countNotOpenedByPetId(@Param("petId") Long petId);
}
