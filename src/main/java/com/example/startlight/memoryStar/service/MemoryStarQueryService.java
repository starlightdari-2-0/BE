package com.example.startlight.memoryStar.service;

import com.example.startlight.kakao.util.UserUtil;
import com.example.startlight.memComment.service.MemCommentService;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.member.service.MemberService;
import com.example.startlight.memoryStar.dao.MemoryStarDao;
import com.example.startlight.memoryStar.dto.MemoryStarRepDto;
import com.example.startlight.memoryStar.dto.ReactionDto;
import com.example.startlight.memoryStar.entity.MemoryStar;
import com.example.startlight.memoryStar.mapper.MemoryStarMapper;
import com.example.startlight.s3.service.S3Service;
import com.example.startlight.starReaction.entity.ReactionType;
import com.example.startlight.starReaction.entity.StarReaction;
import com.example.startlight.starReaction.repository.StarReactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

    @Transactional
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
}
