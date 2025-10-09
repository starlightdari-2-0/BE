package com.example.startlight.pet.dao;

import com.example.startlight.pet.dto.PetReqDto;
import com.example.startlight.pet.entity.Edge;
import com.example.startlight.pet.entity.Pet;
import com.example.startlight.pet.repository.PetRepository;
import com.example.startlight.s3.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PetDaoImpl implements PetDao{

    private final PetRepository petRepository;
    private final S3Service s3Service;
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
    public Pet updatePet(Long petId, PetReqDto petUpdateReqDto) throws IOException {
        Optional<Pet> selectedPet = petRepository.findById(petId);
        if(selectedPet.isPresent()) {
            Pet pet = selectedPet.get();
            //Optional.ofNullable(petUpdateReqDto.getAnimal_type()).ifPresent(pet::setAnimal_type);
            Optional.ofNullable(petUpdateReqDto.getPet_name()).ifPresent(pet::setPet_name);
            Optional.ofNullable(petUpdateReqDto.getSpecies()).ifPresent(pet::setSpecies);
            Optional.ofNullable(petUpdateReqDto.getGender()).ifPresent(pet::setGender);
            Optional.ofNullable(petUpdateReqDto.getBirth_date()).ifPresent(pet::setBirth_date);
            Optional.ofNullable(petUpdateReqDto.getDeath_date()).ifPresent(pet::setDeath_date);
            Optional.ofNullable(petUpdateReqDto.getPersonality()).ifPresent(pet::setPersonality);
            Optional.ofNullable(petUpdateReqDto.getNickname()).ifPresent(pet::setNickname);
            Optional.ofNullable(petUpdateReqDto.getContext()).ifPresent(pet::setContext);

            if (petUpdateReqDto.getPet_img() != null) {
                s3Service.uploadPetImg(petUpdateReqDto.getPet_img(), String.valueOf(petId));
            }
            return pet;
        }
        throw new NoSuchElementException("Member not found with id: " + petId);
    }

    @Override
    public void deletePet(Long petId) {
        Pet selectedPet = selectPet(petId);
        petRepository.delete(selectedPet);
    }

    @Override
    public Long getPetConId(Long petId) {
        Pet selectedPet = selectPet(petId);
        return selectedPet.getCon_id();
    }
}
