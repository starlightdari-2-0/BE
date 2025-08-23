package com.example.startlight.pet.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.startlight.kakao.util.UserUtil;
import com.example.startlight.member.repository.MemberRepository;
import com.example.startlight.pet.dao.PetDao;
import com.example.startlight.pet.dto.*;
import com.example.startlight.pet.entity.Edge;
import com.example.startlight.pet.entity.Pet;
import com.example.startlight.s3.service.S3Service;
import com.example.startlight.starList.dto.StarListRepDto;
import com.example.startlight.starList.service.StarListService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PetServiceImpl implements PetService{
    private final PetDao petDao;
    private final StarListService starListService;
    private final S3Service s3Service;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public PetIdRepDto createPet(PetReqDto petReqDto) throws IOException {
        Long userId = UserUtil.getCurrentUserId();
        Pet pet = petDao.createPet(Pet.toEntity(petReqDto, userId, memberRepository));
        String uploadFile = s3Service.uploadPetImg(petReqDto.getPet_img(), String.valueOf(pet.getPet_id()));
        pet.setPet_img(uploadFile);

        return PetIdRepDto.builder().petId(pet.getPet_id()).build();
    }

    @Override
    public PetRepDto updatePet(Long petId, PetUpdateReqDto petUpdateReqDto) {
        Pet pet = petDao.updatePet(petId, petUpdateReqDto);
        return PetRepDto.toDto(pet);
    }

    @Override
    public List<PetMyPageRepDto> getPets() {
        Long userId = UserUtil.getCurrentUserId();
        List<Pet> pets = petDao.selectAllPet(userId);
        return pets.stream()
                .map(PetMyPageRepDto::toPetMyPageRepDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PetSimpleRepDto> getPetSimple(Long userId) {
        List<Pet> pets = petDao.selectAllPet(userId);
        return pets.stream()
                .map(PetSimpleRepDto::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PetStarListRepDto getPetStarList(Long petId) throws AccessDeniedException {
        Long userId = UserUtil.getCurrentUserId();

        // ✅ 펫 조회 후 없으면 예외 발생
        Pet selectedPet = petDao.selectPet(petId);
        if (selectedPet == null) {
            throw new NotFoundException("Pet with id " + petId + " not found");
        }

        // ✅ 권한 확인
        if (!selectedPet.getMember().getMember_id().equals(userId)) {
            throw new AccessDeniedException("You do not have permission to access this pet");
        }

        // ✅ Edges, StarList 조회
        List<Edge> edgesByPetId = petDao.getEdgesByPetId(petId);
        List<StarListRepDto> list = starListService.getList(petId);

        // ✅ 응답 객체 생성 및 반환
        String svgPath = selectedPet.getSvg_path();
        return PetStarListRepDto.builder()
                .petId(petId)
                .petName(selectedPet.getPet_name())
                .svgPath(svgPath)
                .starList(list)
                .edges(edgesByPetId)
                .build();
    }

    @Override
    public void deletePet(Long petId) {
        petDao.deletePet(petId);
    }


}
