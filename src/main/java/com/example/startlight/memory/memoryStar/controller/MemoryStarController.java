package com.example.startlight.memory.memoryStar.controller;

import com.example.startlight.memory.memoryStar.dto.MemoryStarRepDto;
import com.example.startlight.memory.memoryStar.dto.MemoryStarRepWithComDto;
import com.example.startlight.memory.memoryStar.dto.MemoryStarReqDto;
import com.example.startlight.memory.memoryStar.dto.MemoryStarUpdateDto;
import com.example.startlight.memoryStar.dto.*;
import com.example.startlight.memory.memoryStar.service.MemoryStarLikeService;
import com.example.startlight.memory.memoryStar.service.MemoryStarQueryService;
import com.example.startlight.memory.memoryStar.service.MemoryStarService;
import com.example.startlight.memory.starReaction.entity.ReactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/memory-stars")
public class MemoryStarController {
    private final MemoryStarService memoryStarService;
    private final MemoryStarQueryService memoryStarQueryService;
    private final MemoryStarLikeService memoryStarLikeService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MemoryStarRepDto> createMemoryStar(
            @ModelAttribute MemoryStarReqDto memoryStarReqDto
    ) throws IOException {
        MemoryStarRepDto memoryStar = memoryStarService.createMemoryStar(memoryStarReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(memoryStar);
    }

    //글만 조회
    @GetMapping("/{memoryId}")
    public ResponseEntity<MemoryStarRepDto> selectMemoryStarByMemId(@PathVariable Long memoryId) {
        MemoryStarRepDto starById = memoryStarQueryService.getStarById(memoryId);
        return ResponseEntity.status(HttpStatus.OK).body(starById);
    }

    @PatchMapping("/{memoryId}")
    public ResponseEntity<MemoryStarRepWithComDto> updateMemoryStar(
            @PathVariable Long memoryId,
            @ModelAttribute MemoryStarUpdateDto memoryStarReqDto
    ) throws IOException {
        MemoryStarRepWithComDto memoryStar = memoryStarService.updateMemoryStar(memoryId, memoryStarReqDto);
        return ResponseEntity.status(HttpStatus.OK).body(memoryStar);
    }

    @DeleteMapping("/{memoryId}")
    public ResponseEntity<String> deleteMemoryStar(
            @PathVariable Long memoryId
    ) {
        memoryStarService.deleteMemoryStar(memoryId);
        return ResponseEntity.status(HttpStatus.OK).body("Success delete memory star id : " + memoryId);
    }

//    @GetMapping("/public")
//    public ResponseEntity<List<MemoryStarSimpleRepDto>> getAllMemoryStar() {
//        List<MemoryStarSimpleRepDto> allPublicMemoryStar = memoryStarService.findAllPublicMemoryStar();
//        return ResponseEntity.status(HttpStatus.OK).body(allPublicMemoryStar);
//    }
//
//    @GetMapping()
//    public ResponseEntity<MemoryStarListWithNumDto> getMyMemoryStar() {
//        List<MemoryStarSimpleRepDto> allPublicMemoryStar = memoryStarService.findAllMyMemoryStar();
//        Integer memoryNumber = memberService.getMemoryNumber();
//        MemoryStarListWithNumDto buildDto = MemoryStarListWithNumDto.builder()
//                .memoryNumber(memoryNumber)
//                .memoryStarSimpleRepDtoList(allPublicMemoryStar)
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(buildDto);
//    }
//
    //like

    @PostMapping("/{memoryId}/reactions/{type}")
    public ResponseEntity<ReactionToggleResponse> createLikeMemoryStar(
            @PathVariable Long memoryId,
            @PathVariable ReactionType type) {
        boolean result = memoryStarLikeService.addReaction(memoryId, type);
        return ResponseEntity.status(HttpStatus.OK).body(new ReactionToggleResponse(result));
    }

    @DeleteMapping("/{memoryId}/reactions/{type}")
    public ResponseEntity<ReactionToggleResponse> deleteLikeMemoryStar(
            @PathVariable Long memoryId,
            @PathVariable ReactionType type) {
        boolean result = memoryStarLikeService.removeReaction(memoryId, type);
        return ResponseEntity.status(HttpStatus.OK).body(new ReactionToggleResponse(result));
    }

    public record ReactionToggleResponse(boolean liked) {}
//
//    //comments
//    @GetMapping("/{memoryId}/comments")
//    public ResponseEntity<List<MemCommentRepDto>> getAllComments(@PathVariable Long memoryId) {
//        List<MemCommentRepDto> allByMemoryId = memCommentService.findAllByMemoryId(memoryId);
//        return ResponseEntity.status(HttpStatus.OK).body(allByMemoryId);
//    }
}
