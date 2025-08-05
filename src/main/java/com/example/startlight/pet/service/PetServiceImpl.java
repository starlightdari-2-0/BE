package com.example.startlight.pet.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.example.startlight.kakao.util.UserUtil;
import com.example.startlight.member.repository.MemberRepository;
import com.example.startlight.memoryAlbum.service.MemoryAlbumScheduleService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PetServiceImpl implements PetService{
    private final PetDao petDao;
    private final StarListService starListService;
    private final S3Service s3Service;
    private final FlaskService flaskService;
    private final MemberRepository memberRepository;
    private final MemoryAlbumScheduleService memoryAlbumScheduleService;

    @Override
    @Transactional
    public PetIdRepDto createPet(PetReqDto petReqDto) throws IOException {
        Long userId = UserUtil.getCurrentUserId();
        Pet pet = petDao.createPet(Pet.toEntity(petReqDto, userId, memberRepository));
        String uploadFile = s3Service.uploadPetImg(petReqDto.getPet_img(), String.valueOf(pet.getPet_id()));
        pet.setPet_img(uploadFile);

        // Step 1: 첫 번째 Flask API 호출
        String flaskApiUrl = flaskService.apiUrl + "/stars_run_pidinet";
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("image_url", uploadFile);

        ResponseEntity<String> response = flaskService.sendPostRequest(flaskApiUrl, requestBody, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("✅ Flask 서버에서 응답 성공: " + response.getBody());
            // Step 2: 응답이 200일 경우 추가 Flask API 호출
            log.info("x,y double : " + String.valueOf(petReqDto.getSelected_x()) + String.valueOf(petReqDto.getSelected_y()));
            int x = petReqDto.getSelected_x().intValue();
            int y = petReqDto.getSelected_y().intValue();
            log.info("x,y int : " + String.valueOf(x) + String.valueOf(y));

            FlaskResponseDto flaskResponseDto = flaskService.processImgFlaskApi(uploadFile, x, y);
            System.out.println("✅ 추가 Flask 응답 성공: " + flaskResponseDto.toString());

            //별자리 정보 저장
            pet.setSvg_path(flaskResponseDto.getSvgPath());
            List<Edge> edges = flaskResponseDto.getEdges().stream()
                    .map(e -> new Edge(e.get(0), e.get(1)))
                    .collect(Collectors.toList());
            pet.setEdges(edges);
            List<StarListRepDto> list = starListService.createList(pet.getPet_id(), flaskResponseDto.getMajorPoints());

            //pet 생성 시 random scheduling 호출
            memoryAlbumScheduleService.createAlbumRandom(pet.getPet_id());

            return PetIdRepDto.builder()
                    .petId(pet.getPet_id()).build();
        } else {
            System.out.println("❌ Flask 서버에서 응답 실패: " + response.getStatusCode());
            throw new RuntimeException("Flask 서버 응답 실패");
        }
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
