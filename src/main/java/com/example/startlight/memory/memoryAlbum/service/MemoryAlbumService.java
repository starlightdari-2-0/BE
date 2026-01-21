package com.example.startlight.memory.memoryAlbum.service;

import com.example.startlight.infra.kakao.util.UserUtil;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.member.entity.Member;
import com.example.startlight.memory.memoryAlbum.dao.MemoryAlbumDao;
import com.example.startlight.memory.memoryAlbum.dto.*;
import com.example.startlight.memoryAlbum.dto.*;
import com.example.startlight.memory.memoryAlbum.entity.MemoryAlbum;
import com.example.startlight.memory.memoryAlbum.repository.MemoryAlbumRepository;
import com.example.startlight.memory.memoryStar.repository.MemoryStarRepository;
import com.example.startlight.pet.dao.PetDao;
import com.example.startlight.pet.entity.Pet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoryAlbumService {
    private final MemoryAlbumRepository memoryAlbumRepository;
    private final MemoryAlbumDao memoryAlbumDao;
    private final PetDao petDao;
    private final MemberDao memberDao;
    private final MemoryStarRepository memoryStarRepository;

    public List<AlbumByPetRepDto> getMemoryAlbumStatusByPet() {
        Long userId = UserUtil.getCurrentUserId();
        List<Pet> pets = petDao.selectAllPet(userId);
        List<AlbumByPetRepDto> albumByPetRepDtos = new ArrayList<>();
        for (Pet pet : pets) {
                AlbumByPetRepDto petRepDto = AlbumByPetRepDto.builder()
                        .petId(pet.getPet_id())
                        .petName(pet.getPet_name())
                        .imgUrl(pet.getPet_img())
                        .arrived(false)
                        .arrivedCount(0)
                        .build();
                albumByPetRepDtos.add(petRepDto);
        }
       return albumByPetRepDtos;
    }

    public List<MemoryAlbumSimpleDto> getMemoryAlbumByPet(Long petId) {
        List<MemoryAlbum> byPetId = memoryAlbumDao.findByPetId(petId);
        return byPetId.stream()
                .map(this::toSimpleDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MemoryAlbumRepDto getMemoryAlbumAndUpdateRead(Long letterId) {
        MemoryAlbum byId = memoryAlbumDao.findById(letterId);
        byId.setOpened();
        return toResponseDto(byId);

    }

    private MemoryAlbumSimpleDto toSimpleDto(MemoryAlbum memoryAlbum) {
        return MemoryAlbumSimpleDto.builder()
                .letter_id(memoryAlbum.getLetter_id())
                .pet_id(memoryAlbum.getPet().getPet_id()) // Pet 엔티티에서 ID 매핑
                .title(memoryAlbum.getTitle())
                .content(memoryAlbum.getContent())
                .createdAt(memoryAlbum.getCreatedAt())
                .opened(memoryAlbum.getOpened())
                .build();
    }

    public MemoryAlbumRepDto createMemoryAlbum(Long petId, LetterGeneratedFileRepDto letterGeneratedFileRepDto) {
        Pet selectedPet = petDao.selectPet(petId);
        MemoryAlbum memoryAlbum = MemoryAlbum.builder()
                .pet(selectedPet)
                .content(letterGeneratedFileRepDto.getLetter())
                .images(letterGeneratedFileRepDto.getImages())
                .title(letterGeneratedFileRepDto.getTitle())
                .build();
        MemoryAlbum createdAlbum = memoryAlbumDao.createMemoryAlbum(memoryAlbum);
        return toResponseDto(createdAlbum);
    }

    public MemoryAlbumRepDto createMemoryAlbumRandom(Long petId, LetterGenerateRepDto letterGenerateRepDto) {
        Pet selectedPet = petDao.selectPet(petId);
        MemoryAlbum memoryAlbum = MemoryAlbum.builder()
                .pet(selectedPet)
                .content(letterGenerateRepDto.getLetter())
                .title(letterGenerateRepDto.getTitle())
                .build();
        MemoryAlbum createdAlbum = memoryAlbumDao.createMemoryAlbum(memoryAlbum);
        return toResponseDto(createdAlbum);
    }

    private MemoryAlbumRepDto toResponseDto(MemoryAlbum memoryAlbum) {
        return MemoryAlbumRepDto.builder()
                .letter_id(memoryAlbum.getLetter_id())
                .pet_id(memoryAlbum.getPet().getPet_id())
                .title(memoryAlbum.getTitle())
                .content(memoryAlbum.getContent())
                .images(memoryAlbum.getImages())
                .createdAt(memoryAlbum.getCreatedAt())
                .opened(memoryAlbum.getOpened()).build();
    }

//    public LetterGenerateWithFileReqDto generateDtoWithFile(Long petId) {
//        Pet selectedPet = petDao.selectPet(petId);
//        Long userId = UserUtil.getCurrentUserId();
//        Member member = memberDao.selectMember(userId);
//
//        Pageable pageable = PageRequest.of(0, 1);
//        List<MemoryStar> unusedMemory = memoryStarRepository.findMemoryStarByPetIdUnused(petId, pageable);
//        if(!unusedMemory.isEmpty()) {
//            MemoryStar memoryStar = unusedMemory.get(0);
//            //memoryStar.updateUsedToGenerate();
//
//            List<String> texts = new ArrayList<>();
//            String text = memoryStar.getContent();
//            texts.add(text);
//            return LetterGenerateWithFileReqDto.builder()
//                    .character(selectedPet.getPersonality().getDescription())
//                    .breed(selectedPet.getSpecies())
//                    .texts(texts)
//                    .pet_id(selectedPet.getPet_id())
//                    .pet_name(selectedPet.getPet_name())
//                    .member_name(member.getSt_nickname())
//                    .nickname(selectedPet.getNickname())
//                    .build();
//        }
//        else {
//            return null;
//        }
//    }

    public LetterGenerateWithFileReqDto generateDtoBirthDeath(Long petId, Integer num) {
        Pet selectedPet = petDao.selectPet(petId);
        Long userId = UserUtil.getCurrentUserId();
        Member member = memberDao.selectMember(userId);

        List<String> texts = new ArrayList<>();
        if(num == 1) {
            texts.add("생일");
        }
        else if(num == 2) {
            texts.add("기일");
        }

        return LetterGenerateWithFileReqDto.builder()
                    .character(selectedPet.getPersonality().getDescription())
                    .breed(selectedPet.getSpecies())
                    .texts(texts)
                    .pet_id(selectedPet.getPet_id())
                    .pet_name(selectedPet.getPet_name())
                    .member_name(member.getSt_nickname())
                    .nickname(selectedPet.getNickname())
                    .build();
    }

    public LetterGenerateReqDto generateDtoRandom(Long petId) {
        Pet selectedPet = petDao.selectPet(petId);
        Long userId = UserUtil.getCurrentUserId();
        Member member = memberDao.selectMember(userId);

        return LetterGenerateReqDto.builder()
                .character(selectedPet.getPersonality().getDescription())
                .breed(selectedPet.getSpecies())
                .pet_name(selectedPet.getPet_name())
                .member_name(member.getSt_nickname())
                .nickname(selectedPet.getNickname())
                .build();
    }
}
