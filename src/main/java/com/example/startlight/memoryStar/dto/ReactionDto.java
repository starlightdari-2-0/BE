package com.example.startlight.memoryStar.dto;

public record ReactionDto(
        String type,     // "LIKE1"
        Integer count,      // 3
        boolean isLiked  // true
) {
}
