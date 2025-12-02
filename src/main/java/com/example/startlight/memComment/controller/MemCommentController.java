package com.example.startlight.memComment.controller;

import com.example.startlight.likes.service.LikesService;
import com.example.startlight.memComment.dto.MemCommentRepDto;
import com.example.startlight.memComment.dto.MemCommentReqDto;
import com.example.startlight.memComment.dto.MemCommentUpdateReqDto;
import com.example.startlight.memComment.dto.PageResponse;
import com.example.startlight.memComment.service.MemCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<PageResponse<MemCommentRepDto>> getMemComment(
            @PathVariable("memoryId") Long memoryId,
            @RequestParam(defaultValue = "0") int page) {
        PageResponse<MemCommentRepDto> allByMemoryId = memCommentService.findParentCommentByMemoryId(memoryId, page);
        return ResponseEntity.status(HttpStatus.OK).body(allByMemoryId);
    }

    @GetMapping("/memory-comments/{commentId}/replies")
    public ResponseEntity<List<MemCommentRepDto>> getMemCommentChildren(
            @PathVariable("commentId") Long commentId) {
        List<MemCommentRepDto> comments = memCommentService.findChildrenCommentByCommentId(commentId);
        return ResponseEntity.status(HttpStatus.OK).body(comments);
    }

    @PostMapping("/memory-comments")
    public ResponseEntity<MemCommentRepDto> createMemComment(@RequestBody MemCommentReqDto memCommentReqDto) {
        MemCommentRepDto memCommentRepDto = memCommentService.saveMemComment(memCommentReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(memCommentRepDto);
    }

    @PutMapping("/memory-comments")
    public ResponseEntity<MemCommentRepDto> updateMemComment(@RequestBody MemCommentUpdateReqDto memCommentReqDto) {
        MemCommentRepDto memCommentRepDto = memCommentService.updateMemComment(memCommentReqDto);
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
}
