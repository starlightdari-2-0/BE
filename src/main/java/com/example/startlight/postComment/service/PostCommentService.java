package com.example.startlight.postComment.service;

import com.example.startlight.kakao.util.UserUtil;
import com.example.startlight.member.dao.MemberDao;
import com.example.startlight.member.entity.Member;
import com.example.startlight.post.dao.PostDao;
import com.example.startlight.postComment.dao.PostCommentDao;
import com.example.startlight.postComment.dto.PostCommentRepDto;
import com.example.startlight.postComment.dto.PostCommentReqDto;
import com.example.startlight.postComment.entity.PostComment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommentService {
    private final PostCommentDao postCommentDao;
    private final PostDao postDao;
    private final MemberDao memberDao;

    public PostCommentRepDto createPostComment(PostCommentReqDto postCommentReqDto) {
        try {
            Long userId = UserUtil.getCurrentUserId();
            Member member = memberDao.selectMember(userId);

            if (member == null) {
                throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
            }

            // 댓글 객체 생성
            PostComment postComment = PostComment.toEntity(postCommentReqDto, postDao, member);
            PostComment postComment1 = postCommentDao.createPostComment(postComment);
            return PostCommentRepDto.toDto(postComment1);

        } catch (Exception e) {
            throw new RuntimeException("댓글 작성 중 오류 발생", e);
        }
    }

    public void deletePostComment(Long commentId) {
        Long userId = UserUtil.getCurrentUserId();
        postCommentDao.deletePostComment(commentId, userId);
    }

    public List<PostCommentRepDto> getAllComments(Long postId) {
        List<PostComment> allPostComment = postCommentDao.getAllPostComment(postId);
        return allPostComment.stream()
                .map(PostCommentRepDto::toDto)
                .collect(Collectors.toList());
    }
}
