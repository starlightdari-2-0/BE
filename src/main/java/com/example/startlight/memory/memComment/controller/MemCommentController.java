package com.example.startlight.memory.memComment.controller;

import com.example.startlight.global.dto.CommentRepDto;
import com.example.startlight.global.dto.CommentReqDto;
import com.example.startlight.global.dto.CommentUpdateReqDto;
import com.example.startlight.likes.service.LikesService;
import com.example.startlight.global.response.PageResponse;
import com.example.startlight.memory.memComment.service.MemCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemCommentController {
    private final MemCommentService memCommentService;
    private final LikesService likesService;

    @GetMapping("/memory-stars/{memoryId}/comments")
    public ResponseEntity<PageResponse<CommentRepDto>> getMemComment(
            @PathVariable("memoryId") Long memoryId,
            @RequestParam(defaultValue = "0") int page) {
        PageResponse<CommentRepDto> allByMemoryId = memCommentService.findParentCommentByMemoryId(memoryId, page);
        return ResponseEntity.status(HttpStatus.OK).body(allByMemoryId);
    }

    @GetMapping("/memory-comments/{commentId}/replies")
    public ResponseEntity<List<CommentRepDto>> getMemCommentChildren(
            @PathVariable("commentId") Long commentId) {
        List<CommentRepDto> comments = memCommentService.findChildrenCommentByCommentId(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @PostMapping("/memory-comments")
    public ResponseEntity<CommentRepDto> createMemComment(@RequestBody CommentReqDto memCommentReqDto) {
        CommentRepDto memCommentRepDto = memCommentService.saveMemComment(memCommentReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(memCommentRepDto);
    }

    @PutMapping("/memory-comments")
    public ResponseEntity<CommentRepDto> updateMemComment(@RequestBody CommentUpdateReqDto memCommentReqDto) {
        CommentRepDto memCommentRepDto = memCommentService.updateMemComment(memCommentReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(memCommentRepDto);
    }

    @DeleteMapping("/memory-comments/{commentId}")
    public ResponseEntity<String> deleteMemComment(@PathVariable Long commentId) {
        memCommentService.deleteMemComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).body("success delete comment id : " + commentId);
    }

    // 좋아요
    @PostMapping("/memory-comments/{commentId}/like")
    public ResponseEntity<String> likeMemComment(@PathVariable Long commentId) {
        likesService.createLike(commentId);
        return ResponseEntity.status(HttpStatus.OK).body("success like comment id : " + commentId);
    }

    @DeleteMapping("/memory-comments/{commentId}/like")
    public ResponseEntity<String> unLikeMemComment(@PathVariable Long commentId) {
        likesService.deleteLike(commentId);
        return ResponseEntity.status(HttpStatus.OK).body("success unlike comment id : " + commentId);
    }
}
