package com.example.startlight.memory.memComment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemCommentReqDto {
    private String content;
    private Long memory_id;
    private Long parent_id;
}
