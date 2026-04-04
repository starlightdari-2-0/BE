package com.example.startlight.community.post.service;

import com.example.startlight.community.funeral.dao.FuneralDao;
import com.example.startlight.community.post.dto.*;
import com.example.startlight.community.post.postComment.repository.PostCommentRepository;
import com.example.startlight.community.post.postReaction.entity.PostReaction;
import com.example.startlight.community.post.postReaction.repository.PostReactionRepository;
import com.example.startlight.community.post.repository.PostRepository;
import com.example.startlight.constellation.entity.AnimalCategory;
import com.example.startlight.global.entity.ReactionType;
import com.example.startlight.global.response.PageResponse;
import com.example.startlight.infra.kakao.util.UserUtil;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.member.entity.Member;
import com.example.startlight.community.post.dao.PostDao;
import com.example.startlight.community.post.entity.Category;
import com.example.startlight.community.post.entity.Post;
import com.example.startlight.infra.s3.service.S3Service;
import com.example.startlight.member.repository.MemberRepository;
import com.example.startlight.memory.memoryStar.dto.MemoryStarPublicRepDto;
import com.example.startlight.memory.memoryStar.dto.ReactionDto;
import com.example.startlight.memory.memoryStar.entity.MemoryStar;
import com.example.startlight.memory.memoryStar.service.MemoryStarQueryService;
import com.example.startlight.memory.starReaction.entity.StarReaction;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final PostDao postDao;
    private final MemberDao memberDao;
    private final S3Service s3Service;
    private final PostReactionRepository postReactionRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final MemberRepository memberRepository;

    public PostResponseDto createPost(PostRequestDto postRequestDto) throws IOException {
        Long userId = UserUtil.getCurrentUserId();
        Member member = memberDao.selectMember(userId);
        Post post = Post.toEntity(postRequestDto, member);
        Post createdPost = postDao.createPost(post);
        if (postRequestDto.getImage() != null) {
            String postImgUrl = s3Service.uploadPostImg(postRequestDto.getImage(), createdPost.getPost_id());
            createdPost.setImg_url(postImgUrl);
        }
        return PostResponseDto.toResponseDto(createdPost);
    }

    @Transactional
    public PostDetailedRepDto getPost(Long postId) {
        Post post = postDao.findPostById(postId);
        Long userId = UserUtil.getCurrentUserId();

        ReactionsInfo reactionsInfo = buildReactionsInfo(post, userId);

        return PostDetailedRepDto.builder()
                .post_id(post.getPost_id())
                .writer(post.getMember().getSt_nickname())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .img_url(post.getImg_url())
                .updatedAt(post.getUpdatedAt())
                .updated(post.getUpdated())
                .totalLikes(reactionsInfo.totalLikes)
                .reactions(reactionsInfo.reactions())
                .commentNumber(post.getCommentNumber())
                .build();

    }

    public List<PostResponseDto> getAllPosts(String category) {
        List<Post> allPost = postDao.findAllPost();
        return allPost.stream()
                .filter(post -> {
                    if (category == null || category.equalsIgnoreCase("all")) return true;
                    try {
                        return post.getCategory().equals(Category.valueOf(category.toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        return false;  // 잘못된 category 값이면 필터링
                    }
                })
                .map(PostResponseDto::toResponseDto)  // Post → PostRequestDto 변환
                .collect(Collectors.toList());
    }

//    public PostDetailedRepDto updatePost(PostUpdateReqDto postRequestDto) throws IOException {
//        Long userId = UserUtil.getCurrentUserId();
//        Post post = postDao.updatePost(userId, postRequestDto);
//        if(postRequestDto.getImage() != null) {
//            s3Service.deletePostImg(postRequestDto.getPostId());
//            s3Service.uploadPostImg(postRequestDto.getImage(), post.getPost_id());
//        }
//        return PostDetailedRepDto.toDto(post, funeralDao);
//    }

    public void deletePost(Long postId) {
        Long userId = UserUtil.getCurrentUserId();
        postDao.deletePost(userId, postId);
        s3Service.deletePostImg(postId);
    }

    @Transactional
    public PageResponse<PostRepDto> getPosts(int page, int size, Category category) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = postRepository.findByCategorySorted(category, pageable);

        Long userId = UserUtil.getCurrentUserId();

        // DTO 변환
        Page<PostRepDto> dtoPage = posts.map(post -> toRepDto(post, userId));

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

    private PostRepDto toRepDto(Post post, Long userId) {
        // 반응 정보 조회
        ReactionsInfo reactionsInfo = buildReactionsInfo(post, userId);

        // 댓글 수 조회
        Integer commentCount = postCommentRepository.countByPostId(post.getPost_id());
        String profileUrl = memberRepository.getProfileImgUrl(post.getMember().getMember_id());

        return new PostRepDto(
                post.getPost_id(),
                profileUrl,
                post.getMember().getKk_nickname(),
                post.getTitle(),
                post.getContent(),
                reactionsInfo.reactions(),
                commentCount,
                post.getImg_url(),
                post.getUpdatedAt()
        );
    }

    /**
     * 반응 정보 조회 및 구성
     */
    private ReactionsInfo buildReactionsInfo(Post post, Long userId) {
        Set<ReactionType> myReactionTypes = getMyReactionTypes(post.getPost_id(), userId);

        Map<String, ReactionDto> reactions = new LinkedHashMap<>();
        int totalLikes = 0;

        for (ReactionType type : ReactionType.values()) {
            int count = getCountForType(type, post);
            boolean isLiked = myReactionTypes.contains(type);

            reactions.put(type.name(), new ReactionDto(type.name(), count, isLiked));
            totalLikes += count;
        }

        return new ReactionsInfo(reactions, totalLikes);
    }

    /**
     * 사용자가 누른 반응 타입 조회
     */
    private Set<ReactionType> getMyReactionTypes(Long postId, Long userId) {
        List<PostReaction> myReactions =
                postReactionRepository.findByPostIdAndMemberId(postId, userId);

        return myReactions.stream()
                .map(PostReaction::getReactionType)
                .collect(Collectors.toSet());
    }

    /**
     * 반응 타입별 카운트 조회
     */
    private int getCountForType(ReactionType type, Post post) {
        return switch (type) {
            case LIKE1 -> post.getLike1();
            case LIKE2 -> post.getLike2();
            case LIKE3 -> post.getLike3();
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
