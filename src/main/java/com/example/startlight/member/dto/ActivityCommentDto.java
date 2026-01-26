package com.example.startlight.member.dto;

import java.time.LocalDateTime;

public record ActivityCommentDto(
        String content,
        String nickname,
        LocalDateTime updatedAt,
        Integer replyCount
) {
}
