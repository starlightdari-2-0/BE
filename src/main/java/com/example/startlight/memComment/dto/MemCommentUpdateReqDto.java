package com.example.startlight.memComment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemCommentUpdateReqDto {
    private Long comment_id;
    private String content;
}
