package com.example.startlight.memComReply.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemComReplyRepDto {
    private Long reply_id;
    private String content;
    private Long writer_id;
    private String writer_name;
    private Integer likes;
    private Long comment_id;
}
