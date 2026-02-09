package com.example.startlight.community.post.dto;

import com.example.startlight.community.post.entity.Category;
import com.example.startlight.community.post.entity.Post;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostResponseDto {
    private Long post_id;
    private Long writer_id;
    private String writer_name;
    private String writer_image;
    private String title;
    private String content;
    private Category category;

    @Nullable
    private String img_url;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

    public static PostResponseDto toResponseDto(Post post) {
        return PostResponseDto.builder()
                .post_id(post.getPost_id())
                .writer_id(post.getMember().getMember_id())
                .writer_name(post.getMember().getSt_nickname())
                .writer_image(post.getMember().getProfile_img())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .img_url(post.getImg_url())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
