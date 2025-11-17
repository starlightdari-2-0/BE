package com.example.startlight.memoryStar.service;

import com.example.startlight.kakao.util.UserUtil;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.member.entity.Member;
import com.example.startlight.memoryStar.entity.MemoryStar;
import com.example.startlight.memoryStar.repository.MemoryStarRepository;
import com.example.startlight.starReaction.entity.ReactionType;
import com.example.startlight.starReaction.entity.StarReaction;
import com.example.startlight.starReaction.repository.StarReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class MemoryStarLikeService {
    private final MemoryStarRepository memoryStarRepository;
    private final StarReactionRepository starReactionRepository;
    private final MemberDao memberDao;

    @Transactional
    public boolean addReaction(Long memoryId, ReactionType reactionType) {
        MemoryStar star = memoryStarRepository.findById(memoryId)
                .orElseThrow(() -> new IllegalArgumentException("MemoryStar not found. id=" + memoryId));

        Long userId = UserUtil.getCurrentUserId();
        Member member = memberDao.selectMember(userId);

        boolean exists = starReactionRepository
                .findByMemoryIdAndMemberIdAndReactionType(memoryId, userId, reactionType)
                .isPresent();

        if (exists) {
            return false;
        }

        StarReaction reaction = StarReaction.builder()
                .memoryStar(star)
                .member(member)
                .reactionType(reactionType)
                .build();

        starReactionRepository.save(reaction);
        star.increaseLike(reactionType);
        return true;
    }

    @Transactional
    public boolean removeReaction(Long memoryId, ReactionType reactionType) {
        Long userId = UserUtil.getCurrentUserId();
        MemoryStar star = memoryStarRepository.findById(memoryId)
                .orElseThrow(() -> new IllegalArgumentException("MemoryStar not found. id=" + memoryId));

        Optional<StarReaction> existingOpt =
                starReactionRepository.findByMemoryIdAndMemberIdAndReactionType(memoryId, userId, reactionType);

        if (existingOpt.isEmpty()) {
            return true;
        }

        starReactionRepository.delete(existingOpt.get());

        star.decreaseLike(reactionType);
        return false;
    }
}
