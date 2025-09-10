package com.example.startlight.pet.dao;

import com.example.startlight.pet.dto.PetReqDto;
import com.example.startlight.pet.entity.Edge;
import com.example.startlight.pet.entity.Pet;

import java.io.IOException;
import java.util.List;

public interface PetDao {
    Pet createPet(Pet pet);

    List<Pet> selectAllPet(Long memberId);

    Pet selectPet(Long pet_id);

    Pet updatePet(Long petId, PetReqDto petUpdateReqDto) throws IOException;

    void deletePet(Long petId);
}
