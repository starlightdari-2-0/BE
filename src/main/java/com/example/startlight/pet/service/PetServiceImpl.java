package com.example.startlight.pet.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.startlight.constellation.entity.AnimalType;
import com.example.startlight.constellation.repository.AnimalTypeRepository;
import com.example.startlight.infra.kakao.util.UserUtil;
import com.example.startlight.member.entity.Member;
import com.example.startlight.member.repository.MemberRepository;
import com.example.startlight.memory.memoryStar.repository.MemoryStarRepository;
import com.example.startlight.pet.dao.PetDao;
import com.example.startlight.pet.dto.*;
import com.example.startlight.pet.entity.Pet;
import com.example.startlight.infra.s3.service.S3Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PetServiceImpl implements PetService{
    private final PetDao petDao;
    private final S3Service s3Service;
    private final MemberRepository memberRepository;
    private final AnimalTypeRepository animalTypeRepository;
    private final MemoryStarRepository memoryStarRepository;

    @Override
    @Transactional
    public PetIdRepDto createPet(PetReqDto petReqDto) throws IOException {
        Long userId = UserUtil.getCurrentUserId();
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + userId));

        AnimalType animalType = animalTypeRepository.findById(petReqDto.getAnimal_type_id())
                .orElseThrow(() -> new IllegalArgumentException("AnimalType not found: " + petReqDto.getAnimal_type_id()));

        Pet pet = petDao.createPet(Pet.toEntity(petReqDto, member, animalType));
        String uploadFile = s3Service.uploadPetImg(petReqDto.getPet_img(), String.valueOf(pet.getPet_id()));
        pet.setPet_img(uploadFile);

        return PetIdRepDto.builder().petId(pet.getPet_id()).build();
    }

    @Override
    public PetRepDto updatePet(Long petId, PetReqDto petUpdateReqDto) throws IOException {
        Long userId = UserUtil.getCurrentUserId();

        Pet selectedPet = petDao.selectPet(petId);
        if (selectedPet == null) {
            throw new NotFoundException("Pet with id " + petId + " not found");
        }

        if (!selectedPet.getMember().getMember_id().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this pet");
        }

        Pet pet = petDao.updatePet(petId, petUpdateReqDto);
        return PetRepDto.toDto(pet);
    }

    @Override
    public PetRepDto getPetById(Long petId) throws IOException {
        Pet selectedPet = petDao.selectPet(petId);
        return PetRepDto.toDto(selectedPet);
    }

    @Override
    public List<PetMyPageRepDto> getPets() {
        Long userId = UserUtil.getCurrentUserId();
        List<Pet> pets = petDao.selectAllPet(userId);
        // 별 개수 세기
        List<PetMyPageRepDto> dtoList = new ArrayList<>();

        for (Pet pet : pets) {
            Integer memoryStarCount = memoryStarRepository.countMemoryStarByPetId(pet.getPet_id());
            PetMyPageRepDto petDto = PetMyPageRepDto.builder()
                    .pet_id(pet.getPet_id())
                    .pet_name(pet.getPet_name())
                    .pet_img(pet.getPet_img())
                    .star_count(memoryStarCount).build();
            dtoList.add(petDto);
        }
        return dtoList;
    }

    @Override
    public List<PetSimpleRepDto> getPetSimple(Long userId) {
        List<Pet> pets = petDao.selectAllPet(userId);
        return pets.stream()
                .map(PetSimpleRepDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public String getPetName(Long petId) {
        Pet selectedPet = petDao.selectPet(petId);
        return selectedPet.getPet_name();
    }

    @Override
    public Long getPetConId(Long petId) {
        Pet selectedPet = petDao.selectPet(petId);
        return selectedPet.getCon_id();
    }

    @Override
    public void getPetStars(Long petId) {
        Pet selectedPet = petDao.selectPet(petId);
        Long conId = selectedPet.getCon_id();
    }


    @Override
    public void deletePet(Long petId) throws AccessDeniedException {
        Long userId = UserUtil.getCurrentUserId();

        Pet selectedPet = petDao.selectPet(petId);
        if (selectedPet == null) {
            throw new NotFoundException("Pet with id " + petId + " not found");
        }

        if (!selectedPet.getMember().getMember_id().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this pet");
        }
        petDao.deletePet(petId);
        s3Service.deletePetImg(petId);
    }


}
