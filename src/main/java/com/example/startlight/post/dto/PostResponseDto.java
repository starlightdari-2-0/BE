package com.example.startlight.post.dto;

import com.example.startlight.post.entity.Category;
import com.example.startlight.post.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostResponseDto {
    private Long post_id;
    private String title;
    private String content;
    private Category category;

    public static PostResponseDto toResponseDto(Post post) {
        return PostResponseDto.builder()
                .post_id(post.getPost_id())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .build();
    }
}
