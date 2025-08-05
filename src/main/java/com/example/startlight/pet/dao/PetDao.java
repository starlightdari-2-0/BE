package com.example.startlight.pet.dao;

import com.example.startlight.pet.dto.PetUpdateReqDto;
import com.example.startlight.pet.entity.Edge;
import com.example.startlight.pet.entity.Pet;

import java.util.List;

public interface PetDao {
    Pet createPet(Pet pet);

    List<Pet> selectAllPet(Long memberId);

    Pet selectPet(Long pet_id);

    Pet updatePet(Long petId, PetUpdateReqDto petUpdateReqDto);

    List<Edge> getEdgesByPetId(Long petId);

    void deletePet(Long petId);
}
