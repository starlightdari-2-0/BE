package com.example.startlight.community.post.dto;

import com.example.startlight.memory.memoryStar.dto.ReactionDto;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

public record PostRepDto(
        Long postId,
        String profileImgUrl,
        String writerName,
        String title,
        String content,
        Map<String, ReactionDto> reactions,
        Integer commentNumber,
        String imgUrl,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime updatedAt) {
}
