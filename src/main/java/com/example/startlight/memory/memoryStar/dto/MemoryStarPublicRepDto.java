package com.example.startlight.memory.memoryStar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

public record MemoryStarPublicRepDto(
        Long memoryId,
        String profileImgUrl,
        String writerName,
        String name,
        String content,
        Map<String, ReactionDto> reactions,
        Integer commentNumber,
        String imgUrl,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime updatedAt
        ) {
}
