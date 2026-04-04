package com.example.startlight.global.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentReqDto {
    private String content;
    private Long memory_id;
    private Long parent_id;
}
