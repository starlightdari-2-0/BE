package com.example.startlight.global.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUpdateReqDto {
    private Long comment_id;
    private String content;
}
