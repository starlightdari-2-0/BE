package com.example.startlight.community.postComment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostCommentReqDto {
    private Long postId;
    private String content;
}
