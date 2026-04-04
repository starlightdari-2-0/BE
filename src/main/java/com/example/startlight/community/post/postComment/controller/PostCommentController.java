package com.example.startlight.community.post.postComment.controller;

import com.example.startlight.community.post.postComment.service.PostCommentService;
import com.example.startlight.global.dto.CommentRepDto;
import com.example.startlight.global.dto.CommentReqDto;
import com.example.startlight.global.dto.CommentUpdateReqDto;
import com.example.startlight.global.response.PageResponse;
import com.example.startlight.likes.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post-comments")
public class PostCommentController {
    private final PostCommentService postCommentService;
    private final LikesService likesService;

    @GetMapping("/{postId}/comments")
    public ResponseEntity<PageResponse<CommentRepDto>> getPostComment(
            @PathVariable("postId") Long postId,
            @RequestParam(defaultValue = "0") int page) {
        PageResponse<CommentRepDto> allByMemoryId = postCommentService.findParentCommentByPostId(postId, page);
        return ResponseEntity.status(HttpStatus.OK).body(allByMemoryId);
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentRepDto>> getPostCommentChildren(
            @PathVariable("commentId") Long commentId) {
        List<CommentRepDto> comments = postCommentService.findChildrenCommentByCommentId(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @PostMapping()
    public ResponseEntity<CommentRepDto> createPostComment(@RequestBody CommentReqDto memCommentReqDto) {
        CommentRepDto memCommentRepDto = postCommentService.saveComment(memCommentReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(memCommentRepDto);
    }

    @PutMapping()
    public ResponseEntity<CommentRepDto> updatePostComment(@RequestBody CommentUpdateReqDto memCommentReqDto) {
        CommentRepDto memCommentRepDto = postCommentService.updateComment(memCommentReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(memCommentRepDto);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteMemComment(@PathVariable Long commentId) {
        postCommentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).body("success delete comment id : " + commentId);
    }

    // 좋아요
    @PostMapping("/{commentId}/like")
    public ResponseEntity<String> likePostComment(@PathVariable Long commentId) {
        likesService.createPostCommentLike(commentId);
        return ResponseEntity.status(HttpStatus.OK).body("success like comment id : " + commentId);
    }

    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<String> unLikeMemComment(@PathVariable Long commentId) {
        likesService.deleteLike(commentId);
        return ResponseEntity.status(HttpStatus.OK).body("success unlike comment id : " + commentId);
    }
}
