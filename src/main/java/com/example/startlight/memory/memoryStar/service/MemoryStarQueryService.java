package com.example.startlight.memory.memoryStar.service;

import com.example.startlight.global.response.PageResponse;
import com.example.startlight.infra.kakao.util.UserUtil;
import com.example.startlight.member.repository.MemberRepository;
import com.example.startlight.memory.memComment.repository.MemCommentRepository;
import com.example.startlight.memory.memoryStar.dao.MemoryStarDao;
import com.example.startlight.memory.memoryStar.dto.MemoryStarPublicRepDto;
import com.example.startlight.memory.memoryStar.dto.MemoryStarRepDto;
import com.example.startlight.memory.memoryStar.dto.ReactionDto;
import com.example.startlight.memory.memoryStar.entity.MemoryStar;
import com.example.startlight.memory.memoryStar.mapper.MemoryStarMapper;
import com.example.startlight.memory.memoryStar.repository.MemoryStarRepository;
import com.example.startlight.memory.starReaction.entity.ReactionType;
import com.example.startlight.memory.starReaction.entity.StarReaction;
import com.example.startlight.memory.starReaction.repository.StarReactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoryStarQueryService {

    private final MemoryStarDao memoryStarDao;
    private final MemoryStarMapper mapper = MemoryStarMapper.INSTANCE;
    private final StarReactionRepository starReactionRepository;
    private final MemoryStarRepository memoryStarRepository;
    private final MemCommentRepository memCommentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public MemoryStarRepDto getStarById(Long id) {
        MemoryStar memoryStar = memoryStarDao.selectMemoryStarById(id);
        Long userId = UserUtil.getCurrentUserId();

        // reactions 조회 로직 분리
        ReactionsInfo reactionsInfo = buildReactionsInfo(memoryStar, userId);

        MemoryStarRepDto dto = mapper.toDto(memoryStar);
        dto.setReactions(reactionsInfo.reactions());
        dto.setTotalLikes(reactionsInfo.totalLikes());
        return dto;
    }

    /**
     * Public 게시물 목록 조회
     */
    @Transactional
    public PageResponse<MemoryStarPublicRepDto> getPublicStars(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MemoryStar> stars = memoryStarRepository.findByIsPublicTrueOrderByCreatedAtDesc(pageable);

        Long userId = UserUtil.getCurrentUserId();

        // DTO 변환
        Page<MemoryStarPublicRepDto> dtoPage = stars.map(star -> toPublicRepDto(star, userId));

        return new PageResponse<>(
                dtoPage.getContent(),
                dtoPage.isLast(),
                dtoPage.getTotalElements(),
                dtoPage.getTotalPages(),
                dtoPage.isFirst(),
                dtoPage.getSize(),
                dtoPage.getNumber(),
                dtoPage.getNumberOfElements(),
                dtoPage.isEmpty()
        );
    }

    /**
     * Entity -> Public DTO 변환
     */
    private MemoryStarPublicRepDto toPublicRepDto(MemoryStar star, Long userId) {
        // 반응 정보 조회
        ReactionsInfo reactionsInfo = buildReactionsInfo(star, userId);

        // 댓글 수 조회
        Integer commentCount = memCommentRepository.countByMemoryId(star.getMemory_id());
        String profileUrl = memberRepository.getProfileImgUrl(star.getWriter_id());

        return new MemoryStarPublicRepDto(
                star.getMemory_id(),
                profileUrl,
                star.getWriter_name(),
                star.getName(),
                star.getContent(),
                reactionsInfo.reactions(),
                commentCount,
                star.getImg_url(),
                star.getUpdatedAt()
        );
    }

    /**
     * 반응 정보 조회 및 구성
     */
    private ReactionsInfo buildReactionsInfo(MemoryStar memoryStar, Long userId) {
        Set<ReactionType> myReactionTypes = getMyReactionTypes(memoryStar.getMemory_id(), userId);

        Map<String, ReactionDto> reactions = new LinkedHashMap<>();
        int totalLikes = 0;

        for (ReactionType type : ReactionType.values()) {
            Integer count = getCountForType(type, memoryStar);
            boolean isLiked = myReactionTypes.contains(type);

            reactions.put(type.name(), new ReactionDto(type.name(), count, isLiked));
            totalLikes += count;
        }

        return new ReactionsInfo(reactions, totalLikes);
    }

    /**
     * 사용자가 누른 반응 타입 조회
     */
    private Set<ReactionType> getMyReactionTypes(Long memoryId, Long userId) {
        List<StarReaction> myReactions =
                starReactionRepository.findByMemoryIdAndMemberId(memoryId, userId);

        return myReactions.stream()
                .map(StarReaction::getReactionType)
                .collect(Collectors.toSet());
    }

    /**
     * 반응 타입별 카운트 조회
     */
    private Integer getCountForType(ReactionType type, MemoryStar star) {
        return switch (type) {
            case LIKE1 -> star.getLike1();
            case LIKE2 -> star.getLike2();
            case LIKE3 -> star.getLike3();
        };
    }

    /**
     * 반응 정보를 담는 내부 record
     */
    private record ReactionsInfo(
            Map<String, ReactionDto> reactions,
            Integer totalLikes
    ) {}
}
