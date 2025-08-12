package com.example.startlight.memComReply.controller;

import com.example.startlight.memComReply.dto.MemComReplyRepDto;
import com.example.startlight.memComReply.dto.MemComReplyReqDto;
import com.example.startlight.memComReply.entity.MemComReply;
import com.example.startlight.memComReply.service.MemComReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/memory-stars")
public class MemComReplyController {
    private final MemComReplyService memComReplyService;

    @GetMapping("/comment/{commentId}/reply")
    public ResponseEntity<List<MemComReplyRepDto>> getMemComReplies(
            @PathVariable("commentId") Long commentId) {
        List<MemComReplyRepDto> allReplies = memComReplyService.findAllReplies(commentId);
        return ResponseEntity.ok(allReplies);
    }

    @PostMapping("/comment/reply")
    public ResponseEntity<MemComReplyRepDto> createMemComReply(
            @RequestBody MemComReplyReqDto memComReplyReqDto
            ) {
        MemComReplyRepDto reply = memComReplyService.createReply(memComReplyReqDto);
        return ResponseEntity.ok(reply);
    }

    @PatchMapping("/comment/reply/{replyId}")
    public ResponseEntity<MemComReplyRepDto> updateMemComReply(
            @PathVariable("replyId") Long replyId,
            @RequestBody String content
    ) {
        MemComReplyRepDto reply = memComReplyService.updateReply(replyId, content);
        return ResponseEntity.ok(reply);
    }

    @DeleteMapping("/comment/reply/{replyId}")
    public ResponseEntity<Void> deleteMemComReply(@PathVariable("replyId") Long replyId) {
        memComReplyService.deleteReply(replyId);
        return ResponseEntity.noContent().build();
    }
/
    @PostMapping("/comment/reply/{replyId}/like")
    public ResponseEntity<MemComReplyRepDto> likeMemComReply(@PathVariable("replyId") Long replyId) {
        MemComReply memComReply = memComReplyService.pressLike(replyId);
        MemComReplyRepDto memComReplyRepDto = memComReply.toResponseDto();
        return ResponseEntity.ok(memComReplyRepDto);
    }

    @DeleteMapping("/comment/reply/{replyId}/like")
    public ResponseEntity<Void> unlikeMemComReply(@PathVariable("replyId") Long replyId) {
        memComReplyService.deleteLike(replyId);
        return ResponseEntity.noContent().build();
    }
}
