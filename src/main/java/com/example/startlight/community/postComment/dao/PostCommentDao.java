package com.example.startlight.community.postComment.dao;

import com.example.startlight.global.exception.UnauthorizedAccessException;
import com.example.startlight.community.postComment.entity.PostComment;
import com.example.startlight.community.postComment.repository.PostCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class PostCommentDao {
    private final PostCommentRepository postCommentRepository;

    public PostComment createPostComment(PostComment postComment) {
        return postCommentRepository.save(postComment);
    }

    // 각 댓글 조회
    public PostComment getPostComment(Long commentId) {
        return postCommentRepository.findById(commentId).orElseThrow(() -> new NoSuchElementException(commentId + " not found"));
    }

    // 댓글 삭제
    public void deletePostComment(Long commentId, Long userId) {
        PostComment postComment = getPostComment(commentId);
        if (!postComment.getWriter_id().equals(userId)) {
            throw new UnauthorizedAccessException("자신이 작성한 댓글만 삭제할 수 있습니다.");
        }
        postCommentRepository.deleteById(commentId);
    }

    // 각 post의 댓글 전체 조회
    public List<PostComment> getAllPostComment(Long postId) {
        return postCommentRepository.findAllByPostId(postId);
    }
}
