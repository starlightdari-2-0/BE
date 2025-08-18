package com.example.startlight.memoryStar.service;

import com.example.startlight.kakao.util.UserUtil;
import com.example.startlight.memComment.dto.MemCommentRepDto;
import com.example.startlight.memComment.service.MemCommentService;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.member.entity.Member;
import com.example.startlight.member.service.MemberService;
import com.example.startlight.memoryStar.dao.MemoryStarDao;
import com.example.startlight.memoryStar.dto.*;
import com.example.startlight.memoryStar.entity.MemoryStar;
import com.example.startlight.memoryStar.mapper.MemoryStarMapper;
import com.example.startlight.memoryStar.repository.MemoryStarRepository;
import com.example.startlight.pet.dao.PetDao;
import com.example.startlight.pet.entity.Pet;
import com.example.startlight.s3.service.S3Service;
import com.example.startlight.starList.dao.StarListDao;
import com.example.startlight.starList.entity.StarList;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemoryStarService {
    private final MemoryStarDao memoryStarDao;
    private final MemoryStarRepository memoryStarRepository;
    private final StarListDao starListDao;
    private final MemCommentService memCommentService;
    private final MemberService memberService;
    private final MemberDao memberDao;
    private final S3Service s3Service;
    private final MemoryStarMapper mapper = MemoryStarMapper.INSTANCE;
    private final PetDao petDao;

    public MemoryStarRepWithComDto selectStarById(Long id) {
        MemoryStar memoryStar = memoryStarDao.selectMemoryStarById(id);
        Long userId = UserUtil.getCurrentUserId();
        boolean ifLiked = memoryStarDao.findIfLiked(id, userId);
        MemoryStarRepDto mapperDto = mapper.toDto(memoryStar);

        List<MemCommentRepDto> allByMemoryId = memCommentService.findAllByMemoryId(id);

        MemoryStarRepWithComDto dto = MemoryStarRepWithComDto.builder()
                .memoryStarRepDto(mapperDto)
                .memComments(allByMemoryId).build();

        dto.getMemoryStarRepDto().setIsLiked(ifLiked);
        return dto;
    }

    public MemoryStarRepDto getStarById(Long id) {
        MemoryStar memoryStar = memoryStarDao.selectMemoryStarById(id);
        Long userId = UserUtil.getCurrentUserId();
        boolean ifLiked = memoryStarDao.findIfLiked(id, userId);
        MemoryStarRepDto dto = mapper.toDto(memoryStar);
        dto.setIsLiked(ifLiked);
        return dto;
    }


    public MemoryStarRepDto createMemoryStar(MemoryStarReqDto memoryStarReqDto) throws IOException {
        StarList starListById = starListDao.findStarListById(memoryStarReqDto.getStar_id());
        Long userId = UserUtil.getCurrentUserId();
        Member member = memberDao.selectMember(userId);
        memoryStarReqDto.setWriter_id(userId);
        memoryStarReqDto.setWriter_name(member.getSt_nickname());
        MemoryStar memoryStar = mapper.toEntity(memoryStarReqDto, starListById);
        MemoryStar createdStar = memoryStarDao.createMemoryStar(memoryStar);
        String memoryImgUrls = s3Service.uploadMemoryImg(memoryStarReqDto.getImg_url(), createdStar.getMemory_id());
        createdStar.setImg_url(memoryImgUrls);
        starListDao.updateStarWritten(createdStar, memoryStarReqDto.getStar_id());
        memberService.updateMemberMemory();

        //MemoryStar 개수 체크
        Integer countStar = memoryStarRepository.countMemoryStarByPetId(memoryStar.getPet_id());
        Pet selectedPet = petDao.selectPet(memoryStar.getPet_id());

        return mapper.toDto(createdStar);

    }

    public MemoryStarRepWithComDto updateMemoryStar(Long memoryId, MemoryStarUpdateDto memoryStarUpdateDto) throws IOException {
        Long userId = UserUtil.getCurrentUserId();
        MemoryStar memoryStar = memoryStarDao.updateMemoryStar(userId, memoryId, memoryStarUpdateDto);
        if(memoryStarUpdateDto.getImg_url() != null) {
            s3Service.deleteMemoryImg(memoryId);
            String uploadMemoryImg = s3Service.uploadMemoryImg(memoryStarUpdateDto.getImg_url(), memoryId);
            memoryStar.setImg_url(uploadMemoryImg);
        }
        MemoryStarRepDto dto = mapper.toDto(memoryStar);
        List<MemCommentRepDto> allByMemoryId = memCommentService.findAllByMemoryId(memoryStar.getMemory_id());

        return MemoryStarRepWithComDto.builder()
                .memoryStarRepDto(dto)
                .memComments(allByMemoryId).build();
    }

    public void deleteMemoryStar(Long memoryId) {
        Long userId = UserUtil.getCurrentUserId();
        MemoryStar memoryStar = memoryStarDao.selectMemoryStarById(memoryId);
        Long starId = memoryStar.getStarList().getStar_id();
        memoryStarDao.deleteMemoryStarById(userId, memoryStar);
        s3Service.deleteMemoryImg(memoryId);

        //star에서 unwritten 처리
        starListDao.setStarUnWritten(starId);
    }

    public List<MemoryStarSimpleRepDto> findAllPublicMemoryStar() {
        List<MemoryStar> allPublicMemoryStar = memoryStarDao.getAllPublicMemoryStar();
        return mapper.toSimpleRepDtoList(allPublicMemoryStar);
    }

    public List<MemoryStarSimpleRepDto> findAllMyMemoryStar() {
        Long userId = UserUtil.getCurrentUserId();
        List<MemoryStar> allMyMemoryStar = memoryStarDao.getAllMyMemoryStar(userId);
        return mapper.toSimpleRepDtoList(allMyMemoryStar);
    }

    public MemoryStarLikeDto createLike(Long id) {
        Long userId = UserUtil.getCurrentUserId();
        MemoryStar memoryStar = memoryStarDao.pressLike(id, userId);
        return MemoryStarLikeDto.builder()
                .memoryId(memoryStar.getMemory_id())
                .isLiked(true)
                .likes(memoryStar.getLikes())
                .build();
    }

    public MemoryStarLikeDto deleteLike(Long id) {
        Long userId = UserUtil.getCurrentUserId();
        MemoryStar memoryStar = memoryStarDao.deleteLike(id, userId);
        return MemoryStarLikeDto.builder()
                .memoryId(memoryStar.getMemory_id())
                .isLiked(false)
                .likes(memoryStar.getLikes())
                .build();
    }
}
