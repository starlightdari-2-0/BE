package com.example.startlight.memComment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemCommentRepDto {
    private Long comment_id;
    private Long memory_id;
    private String content;
    private Long writer_id;
    private String writer_name;
}
