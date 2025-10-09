package com.example.startlight.pet.repository;

import com.example.startlight.pet.entity.Edge;
import com.example.startlight.pet.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("SELECT p from Pet p where p.member.member_id = :memberId")
    List<Pet> findByMemberId(@Param("memberId") Long memberId);

//    @Query("select p.edges from Pet p where p.pet_id = :petId")
//    List<Edge> findEdgesByPetId(@Param("petId") Long petId);
}
