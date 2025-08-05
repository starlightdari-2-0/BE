package com.example.startlight.starList.repository;

import com.example.startlight.starList.entity.StarList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StarListRepository extends JpaRepository<StarList, Long> {

    @Query("SELECT s from StarList s where s.pet.pet_id = :petId")
    List<StarList> findByPetId(@Param("petId") Long petId);
}
