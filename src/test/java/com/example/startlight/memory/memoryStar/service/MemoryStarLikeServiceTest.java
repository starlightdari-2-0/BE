package com.example.startlight.memory.memoryStar.service;

import com.example.startlight.infra.kakao.util.UserUtil;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.member.entity.Member;
import com.example.startlight.memory.memoryStar.entity.MemoryStar;
import com.example.startlight.memory.memoryStar.repository.MemoryStarRepository;
import com.example.startlight.memory.starReaction.entity.ReactionType;
import com.example.startlight.memory.starReaction.entity.StarReaction;
import com.example.startlight.memory.starReaction.repository.StarReactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemoryStarLikeServiceTest {
    @Mock MemoryStarRepository memoryStarRepository;
    @Mock StarReactionRepository starReactionRepository;
    @Mock MemberDao memberDao;

    @InjectMocks
    MemoryStarLikeService service;

    @Test
    void addReaction_이미존재하면_false_저장안함_카운트증가안함() {
        Long memoryId = 1L;
        Long userId = 10L;
        ReactionType type = ReactionType.LIKE1;

        // MemoryStar는 increaseLike 호출 여부를 검증해야 해서 mock으로 두는 게 편함
        MemoryStar star = mock(MemoryStar.class);
        Member member = mock(Member.class);

        when(memoryStarRepository.findById(memoryId)).thenReturn(Optional.of(star));
        when(memberDao.selectMember(userId)).thenReturn(member);
        when(starReactionRepository.findByMemoryIdAndMemberIdAndReactionType(memoryId, userId, type))
                .thenReturn(Optional.of(mock(StarReaction.class)));

        try (MockedStatic<UserUtil> mocked = mockStatic(UserUtil.class)) {
            mocked.when(UserUtil::getCurrentUserId).thenReturn(userId);

            boolean result = service.addReaction(memoryId, type);

            assertThat(result).isFalse();
            verify(starReactionRepository, never()).save(any(StarReaction.class));
            verify(star, never()).increaseLike(type);
        }
    }

    @Test
    void addReaction_없으면_true_저장하고_카운트증가() {
        Long memoryId = 1L;
        Long userId = 10L;
        ReactionType type = ReactionType.LIKE1;

        MemoryStar star = mock(MemoryStar.class);
        Member member = mock(Member.class);

        when(memoryStarRepository.findById(memoryId)).thenReturn(Optional.of(star));

        when(memberDao.selectMember(userId)).thenReturn(member);

        when(starReactionRepository
                .findByMemoryIdAndMemberIdAndReactionType(memoryId, userId, type))
                .thenReturn(Optional.empty());

        try (MockedStatic<UserUtil> mocked = mockStatic(UserUtil.class)) {
            mocked.when(UserUtil::getCurrentUserId)
                    .thenReturn(userId);

            boolean result = service.addReaction(memoryId, type);

            assertThat(result).isTrue();
            verify(starReactionRepository).save(any(StarReaction.class));
            verify(star).increaseLike(type);
        }
    }

    @Test
    void removeReaction_존재하면_delete하고_카운트감소() {
        Long memoryId = 1L;
        Long userId = 10L;
        ReactionType type = ReactionType.LIKE1;

        MemoryStar star = mock(MemoryStar.class);
        StarReaction existing = mock(StarReaction.class);

        when(memoryStarRepository.findById(memoryId)).thenReturn(Optional.of(star));
        when(starReactionRepository.findByMemoryIdAndMemberIdAndReactionType(memoryId, userId, type))
                .thenReturn(Optional.of(existing));

        try (MockedStatic<UserUtil> mocked = mockStatic(UserUtil.class)) {
            mocked.when(UserUtil::getCurrentUserId).thenReturn(userId);

            boolean result = service.removeReaction(memoryId, type);

            // 현재 코드 기준: 존재하면 delete 수행 후 return false
            assertThat(result).isFalse();

            verify(starReactionRepository).delete(existing);
            verify(star).decreaseLike(type);
        }
    }

    @Test
    void removeReaction_없으면_delete안함_카운트감소안함() {
        Long memoryId = 1L;
        Long userId = 10L;
        ReactionType type = ReactionType.LIKE1;

        MemoryStar star = mock(MemoryStar.class);

        when(memoryStarRepository.findById(memoryId)).thenReturn(Optional.of(star));
        when(starReactionRepository.findByMemoryIdAndMemberIdAndReactionType(memoryId, userId, type))
                .thenReturn(Optional.empty());

        try (MockedStatic<UserUtil> mocked = mockStatic(UserUtil.class)) {
            mocked.when(UserUtil::getCurrentUserId).thenReturn(userId);

            boolean result = service.removeReaction(memoryId, type);

            assertThat(result).isTrue();

            verify(starReactionRepository, never()).delete(any());
            verify(star, never()).decreaseLike(type);
        }
    }

    @Test
    void addReaction_memoryStar없으면_예외() {
        Long memoryId = 999L;
        ReactionType type = ReactionType.LIKE1;

        when(memoryStarRepository.findById(memoryId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addReaction(memoryId, type))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("MemoryStar not found");
    }
}