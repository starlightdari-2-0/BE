package com.example.startlight.memory.memoryStar.service;

import com.example.startlight.infra.kakao.util.UserUtil;
import com.example.startlight.member.repository.MemberRepository;
import com.example.startlight.memory.memComment.dto.MemCommentRepDto;
import com.example.startlight.memory.memComment.service.MemCommentService;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.member.entity.Member;
import com.example.startlight.member.service.MemberService;
import com.example.startlight.memory.memoryStar.dao.MemoryStarDao;
import com.example.startlight.memory.memoryStar.dto.*;
import com.example.startlight.memory.memoryStar.entity.MemoryStar;
import com.example.startlight.memory.memoryStar.mapper.MemoryStarMapper;
import com.example.startlight.infra.s3.service.S3Service;
import com.example.startlight.memory.starReaction.repository.StarReactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private final MemberRepository memberRepository;

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

    public List<MemoryStarSimpleRepDto> findAllMyMemoryStar() {
        Long userId = UserUtil.getCurrentUserId();
        List<MemoryStar> allMyMemoryStar = memoryStarDao.getAllMyMemoryStar(userId);
        return mapper.toSimpleRepDtoList(allMyMemoryStar);
    }
}
