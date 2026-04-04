package com.example.startlight.community.post.postLike;

import com.example.startlight.community.post.entity.Post;
import com.example.startlight.community.post.postReaction.entity.PostReaction;
import com.example.startlight.community.post.postReaction.repository.PostReactionRepository;
import com.example.startlight.community.post.repository.PostRepository;
import com.example.startlight.global.entity.ReactionType;
import com.example.startlight.infra.kakao.util.UserUtil;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostRepository postRepository;
    private final MemberDao memberDao;
    private final PostReactionRepository postReactionRepository;

    @Transactional
    public boolean addReaction(Long postId, ReactionType reactionType) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found. id = "+postId));

        Long userId = UserUtil.getCurrentUserId();
        Member member = memberDao.selectMember(userId);

        boolean exists = postReactionRepository
                .findByPostIdAndMemberIdAndReactionType(postId, userId, reactionType)
                .isPresent();

        if (exists) {
            return false;
        }

        PostReaction reaction = PostReaction.builder()
                .post(post)
                .member(member)
                .reactionType(reactionType)
                .build();

        postReactionRepository.save(reaction);
        post.increaseLike(reactionType);
        return true;
    }

    @Transactional
    public boolean removeReaction(Long postId, ReactionType reactionType) {
        Long userId = UserUtil.getCurrentUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found. id = "+postId));
        Optional<PostReaction> existingOpt =
                postReactionRepository.findByPostIdAndMemberIdAndReactionType(postId, userId, reactionType);

        if (existingOpt.isEmpty()) {
            return true;
        }

        postReactionRepository.delete(existingOpt.get());
        post.decreaseLike(reactionType);
        return false;
    }
}
