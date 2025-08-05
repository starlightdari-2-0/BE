package com.example.startlight.postComment.dto;

import com.example.startlight.postComment.entity.PostComment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostCommentRepDto {

    private Long comment_id;

    private Long post_id;

    private Long writer_id;

    private String writer_name;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdAt;

    public static PostCommentRepDto toDto(PostComment postComment) {
        return PostCommentRepDto.builder()
                .comment_id(postComment.getComment_id())
                .post_id(postComment.getPost().getPost_id())
                .writer_id(postComment.getWriter_id())
                .writer_name(postComment.getWriter_name())
                .content(postComment.getContent())
                .createdAt(postComment.getCreatedAt())
                .build();
    }
}
