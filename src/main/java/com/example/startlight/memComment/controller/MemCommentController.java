package com.example.startlight.memComment.controller;

import com.example.startlight.memComment.dto.MemCommentRepDto;
import com.example.startlight.memComment.dto.MemCommentReqDto;
import com.example.startlight.memComment.dto.MemCommentUpdateReqDto;
import com.example.startlight.memComment.service.MemCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/memory-stars")
@RequiredArgsConstructor
public class MemCommentController {
    private final MemCommentService memCommentService;

    @GetMapping("/{memoryId}/comments")
    public ResponseEntity<List<MemCommentRepDto>> getMemComment(@PathVariable("memoryId") Long memoryId) {
        List<MemCommentRepDto> allByMemoryId = memCommentService.findAllByMemoryId(memoryId);
        return ResponseEntity.status(HttpStatus.OK).body(allByMemoryId);
    }

    @PostMapping("/comment")
    public ResponseEntity<MemCommentRepDto> createMemComment(@RequestBody MemCommentReqDto memCommentReqDto) {
        MemCommentRepDto memCommentRepDto = memCommentService.saveMemComment(memCommentReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(memCommentRepDto);
    }

    @PutMapping("/comment")
    public ResponseEntity<MemCommentRepDto> updateMemComment(@RequestBody MemCommentUpdateReqDto memCommentReqDto) {
        MemCommentRepDto memCommentRepDto = memCommentService.updateMemComment(memCommentReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(memCommentRepDto);
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<String> deleteMemComment(@PathVariable Long commentId) {
        memCommentService.deleteMemComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).body("success delete comment id : " + commentId);
    }
}
