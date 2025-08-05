package com.example.startlight.postComment.controller;

import com.example.startlight.postComment.dto.PostCommentRepDto;
import com.example.startlight.postComment.dto.PostCommentReqDto;
import com.example.startlight.postComment.service.PostCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/post/comments")
public class PostCommentController {
    private final PostCommentService postCommentService;

    @PostMapping()
    public ResponseEntity<PostCommentRepDto> postComment(@RequestBody PostCommentReqDto postCommentReqDto) {
        PostCommentRepDto postComment = postCommentService.createPostComment(postCommentReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(postComment);
    }


    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        postCommentService.deletePostComment(commentId);
        return ResponseEntity.noContent().build();  // 204 No Content 반환
    }
}
