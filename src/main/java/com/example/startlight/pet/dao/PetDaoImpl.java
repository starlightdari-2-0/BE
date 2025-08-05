package com.example.startlight.pet.dao;

import com.example.startlight.pet.dto.PetUpdateReqDto;
import com.example.startlight.pet.entity.Edge;
import com.example.startlight.pet.entity.Pet;
import com.example.startlight.pet.repository.PetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PetDaoImpl implements PetDao{

    private final PetRepository petRepository;
    @Override
    public Pet createPet(Pet pet) {
        return petRepository.save(pet);
    }

    @Override
    public List<Pet> selectAllPet(Long memberId) {
        return petRepository.findByMemberId(memberId);
    }

    @Override
    public Pet selectPet(Long pet_id) {
        Optional<Pet> selectedPet = petRepository.findById(pet_id);
        if(selectedPet.isPresent()) {
            return selectedPet.get();
        }
        throw new NoSuchElementException("Member not found with id: " + pet_id);
    }

    @Override
    @Transactional
    public Pet updatePet(Long petId, PetUpdateReqDto petUpdateReqDto) {
        Optional<Pet> selectedPet = petRepository.findById(petId);
        if(selectedPet.isPresent()) {
            Pet pet = selectedPet.get();
            Optional.ofNullable(petUpdateReqDto.getPet_name()).ifPresent(pet::setPet_name);
            Optional.ofNullable(petUpdateReqDto.getSpecies()).ifPresent(pet::setSpecies);
            Optional.ofNullable(petUpdateReqDto.getGender()).ifPresent(pet::setGender);
            Optional.ofNullable(petUpdateReqDto.getBirth_date()).ifPresent(pet::setBirth_date);
            Optional.ofNullable(petUpdateReqDto.getDeath_date()).ifPresent(pet::setDeath_date);
            Optional.ofNullable(petUpdateReqDto.getPersonality()).ifPresent(pet::setPersonality);
            return pet;
        }
        throw new NoSuchElementException("Member not found with id: " + petId);
    }

    @Override
    public List<Edge> getEdgesByPetId(Long petId) {
        return petRepository.findEdgesByPetId(petId);
    }

    @Override
    public void deletePet(Long petId) {
        Pet selectedPet = selectPet(petId);
        petRepository.delete(selectedPet);
    }
}
