package com.example.startlight.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ActivityPostDto(
        String title,
        String petName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDateTime updatedAt,
        Integer likeCount,
        Integer commentCount
) {
}
