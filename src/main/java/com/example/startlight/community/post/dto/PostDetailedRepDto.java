package com.example.startlight.community.post.dto;

import com.example.startlight.community.funeral.dao.FuneralDao;
import com.example.startlight.community.funeral.entity.Funeral;
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
public class PostDetailedRepDto {
    private Long post_id;
    private String writer;
    private String title;
    private String content;
    private Category category;
    private Long report;

    @Nullable
    private String img_url;

    @Nullable
    private Funeral funeral;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

    private Boolean updated;

    public static PostDetailedRepDto toDto(Post post, FuneralDao funeralDao) {
        if (post.getFuneral_id() == null) {
            return PostDetailedRepDto.builder()
                    .post_id(post.getPost_id())
                    .writer(post.getMember().getSt_nickname())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .category(post.getCategory())
                    .report(post.getReport())
                    .img_url(post.getImg_url())
                    .updatedAt(post.getUpdatedAt())
                    .updated(post.getUpdated())
                    .build();
        }
        else {
            return PostDetailedRepDto.builder()
                    .post_id(post.getPost_id())
                    .writer(post.getMember().getSt_nickname())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .category(post.getCategory())
                    .report(post.getReport())
                    .img_url(post.getImg_url())
                    .funeral(funeralDao.selectById(post.getFuneral_id()))
                    .updatedAt(post.getUpdatedAt())
                    .updated(post.getUpdated())
                    .build();
        }
    }
}
