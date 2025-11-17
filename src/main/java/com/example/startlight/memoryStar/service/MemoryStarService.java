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
import com.example.startlight.starReaction.entity.ReactionType;
import com.example.startlight.starReaction.entity.StarReaction;
import com.example.startlight.starReaction.repository.StarReactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemoryStarService {
    private final MemoryStarDao memoryStarDao;
    private final MemCommentService memCommentService;
    private final MemberService memberService;
    private final MemberDao memberDao;
    private final S3Service s3Service;
    private final MemoryStarMapper mapper = MemoryStarMapper.INSTANCE;
    private final StarReactionRepository starReactionRepository;

    public MemoryStarRepDto getStarById(Long id) {
        MemoryStar memoryStar = memoryStarDao.selectMemoryStarById(id);
        Long userId = UserUtil.getCurrentUserId();

        List<StarReaction> myReactions =
                starReactionRepository.findByMemoryIdAndMemberId(memoryStar.getMemory_id(), userId);

        Set<ReactionType> myTypes = myReactions.stream()
                .map(StarReaction::getReactionType)
                .collect(Collectors.toSet());

        Map<String, ReactionDto> reactions = new LinkedHashMap<>();
        Integer totalLikes = 0;

        for (ReactionType type : ReactionType.values()) {
            Integer count = getCountForType(type, memoryStar);
            boolean isLiked = myTypes.contains(type);

            reactions.put(
                    type.name(),
                    new ReactionDto(type.name(), count, isLiked)
            );
            totalLikes += count;
        }

        MemoryStarRepDto dto = mapper.toDto(memoryStar);
        dto.setReactions(reactions);
        dto.setTotalLikes(totalLikes);
        return dto;
    }

    private Integer getCountForType(ReactionType type, MemoryStar star) {
        return switch (type) {
            case LIKE1 -> star.getLike1();
            case LIKE2 -> star.getLike2();
            case LIKE3 -> star.getLike3();
        };
    }


    public MemoryStarRepDto createMemoryStar(MemoryStarReqDto memoryStarReqDto) throws IOException {
        Long userId = UserUtil.getCurrentUserId();
        Member member = memberDao.selectMember(userId);
        memoryStarReqDto.setWriter_id(userId);
        memoryStarReqDto.setWriter_name(member.getSt_nickname());
        MemoryStar memoryStar = mapper.toEntity(memoryStarReqDto);
        MemoryStar createdStar = memoryStarDao.createMemoryStar(memoryStar);
        String memoryImgUrls = s3Service.uploadMemoryImg(memoryStarReqDto.getImg_url(), createdStar.getMemory_id());
        createdStar.setImg_url(memoryImgUrls);
        memberService.updateMemberMemory();
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
        memoryStarDao.deleteMemoryStarById(userId, memoryStar);
        s3Service.deleteMemoryImg(memoryId);
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
}
