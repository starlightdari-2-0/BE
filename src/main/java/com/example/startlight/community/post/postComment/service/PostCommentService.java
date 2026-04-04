package com.example.startlight.community.post.postComment.service;

import com.example.startlight.community.post.dao.PostDao;
import com.example.startlight.community.post.postComment.entity.PostComment;
import com.example.startlight.community.post.postComment.repository.PostCommentRepository;
import com.example.startlight.global.dto.CommentRepDto;
import com.example.startlight.global.dto.CommentReqDto;
import com.example.startlight.global.dto.CommentUpdateReqDto;
import com.example.startlight.global.response.PageResponse;
import com.example.startlight.infra.kakao.util.UserUtil;
import com.example.startlight.likes.service.LikesService;
import com.example.startlight.member.dao.MemberDao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final MemberDao memberDao;
    private final PostDao postDao;
    private final LikesService likesService;

    /**
     * 댓글/대댓글 생성
     * - MemCommentService.saveMemComment()와 동일한 흐름
     * - 차이점: PostComment는 DAO 없이 Repository만 사용
     */
    public CommentRepDto saveComment(CommentReqDto commentReqDto) {
        Long userId = UserUtil.getCurrentUserId();
        String stNickname = memberDao.selectMember(userId).getSt_nickname();

        // Post 매핑(프로젝트에 맞는 조회 메서드 사용)
        // ※ PostDao에 해당 메서드명이 다르면 프로젝트에 맞게 메서드명만 바꿔주세요.
        var post = postDao.findPostById(commentReqDto.getMemory_id());

        Optional<PostComment> parentComment = Optional.empty();
        if (commentReqDto.getParent_id() != null) {
            parentComment = postCommentRepository.findById(commentReqDto.getParent_id());
            if (parentComment.isEmpty()) {
                throw new IllegalArgumentException("부모 댓글이 존재하지 않습니다.");
            }
            // 부모 댓글과 자식 댓글이 같은 Post에 속하는지 검증
            if (!parentComment.get().getPost().getPost_id().equals(post.getPost_id())) {
                throw new IllegalArgumentException("부모 댓글이 다른 Post에 속해 있습니다.");
            }
        }

        PostComment postComment = PostComment.builder()
                .content(commentReqDto.getContent())
                .writer_id(userId)
                .writer_name(stNickname)
                .post(post)
                .parent(parentComment.get())
                .build();

        PostComment saved = postCommentRepository.save(postComment);
        return toDto(saved);
    }

    /**
     * 댓글 수정
     */
    public CommentRepDto updateComment(CommentUpdateReqDto dto) {
        Long userId = UserUtil.getCurrentUserId();
        Optional<PostComment> postCommentOptional = postCommentRepository.findById(dto.getComment_id());
        if (postCommentOptional.isEmpty()) {
            throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
        }
        if (!userId.equals(postCommentOptional.get().getWriter_id())) {
            throw new IllegalArgumentException("댓글 수정 권한이 없습니다.");
        }
        PostComment postComment = postCommentOptional.get();
        postComment.updateContent(dto.getContent());
        PostComment saved = postCommentRepository.save(postComment);
        return toDto(saved);
    }

    /**
     * 댓글 삭제
     */
    public void deleteComment(Long comment_id) {
        Long userId = UserUtil.getCurrentUserId();
        Optional<PostComment> postComment = postCommentRepository.findById(comment_id);
        if (postComment.isEmpty()) {
            throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
        }
        if (!userId.equals(postComment.get().getWriter_id())) {
            throw new IllegalArgumentException("댓글 삭제 권한이 없습니다.");
        }
        postCommentRepository.delete(postComment.get());
    }

    private Page<PostComment> findParentCommentPageByPostId(Long postId, int page) {
        Pageable pageable = PageRequest.of(page, 5);
        return postCommentRepository.findParentCommentByPostId(postId, pageable);
    }

    /**
     * 부모 댓글 페이징 조회
     * - MemCommentService.findParentCommentByMemoryId()와 동일 역할
     * - Repository 기반으로 구현 (필요 시 PostCommentRepository에 쿼리 메서드 추가)
     */
    public PageResponse<CommentRepDto> findParentCommentByPostId(Long post_id, int page) {
        var commentPage = findParentCommentPageByPostId(post_id, page);

        var dtoPage = commentPage.map(this::toDto);
        var contentWithFlag = dtoPage.getContent().stream()
                .peek(dto -> {
                    boolean mine = checkIfMine(dto.getComment_id());
                    dto.setMine(mine);

                    Long replyCount = postCommentRepository.countChildrenComment(dto.getComment_id());
                    dto.setReply_count(replyCount);

                    Long likeCount = likesService.getLikeCount(dto.getComment_id());
                    dto.setLike_count(likeCount);

                    boolean isLiked = likesService.findIfILiked(dto.getComment_id());
                    dto.setLike(isLiked);
                })
                .toList();

        return new PageResponse<>(
                contentWithFlag,
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
     * Post의 전체 댓글(부모/자식 포함) 조회
     */
    public List<CommentRepDto> findAllByPostId(Long post_id) {
        List<PostComment> allByPostId = postCommentRepository.findAllByPostIdDesc(post_id);
        return allByPostId.stream().map(this::toDto).toList();
    }

    /**
     * 내 댓글인지 확인
     */
    public boolean checkIfMine(Long comment_id) {
        Long userId = UserUtil.getCurrentUserId();
        Optional<PostComment> postComment = postCommentRepository.findById(comment_id);
        if (postComment.isEmpty()) {
            throw new IllegalArgumentException("댓글이 존재하지 않습니다.");
        }
        return userId.equals(postComment.get().getWriter_id());
    }

    /**
     * 특정 댓글의 자식 댓글(대댓글) 조회
     */
    public List<CommentRepDto> findChildrenCommentByCommentId(Long comment_id) {
        List<PostComment> comments = postCommentRepository.findChildrenCommentByCommentId(comment_id);
        List<CommentRepDto> dtos = comments.stream().map(this::toDto).toList();
        for (CommentRepDto dto : dtos) {
            dto.setMine(checkIfMine(dto.getComment_id()));
            Long likeCount = likesService.getLikeCount(dto.getComment_id());
            dto.setLike_count(likeCount);
            boolean ifLiked = likesService.findIfILiked(dto.getComment_id());
            dto.setLike(ifLiked);
        }
        return dtos;
    }

    /**
     * (MemCommentMapper 대체) PostComment -> CommentRepDto 매핑
     * 프로젝트의 CommentRepDto 필드명에 맞게 조정하세요.
     */
    private CommentRepDto toDto(PostComment postComment) {
        return CommentRepDto.builder()
                .comment_id(postComment.getComment_id())
                .content(postComment.getContent())
                .writer_name(postComment.getWriter_name())
                .memory_id(postComment.getPost().getPost_id())
                .build();
    }
}
