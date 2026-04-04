package com.example.startlight.community.post.dto;

import com.example.startlight.community.post.entity.Category;
import com.example.startlight.memory.memoryStar.dto.ReactionDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
public class PostDetailedRepDto {
    private Long post_id;
    private String writer;
    private String title;
    private String content;
    private Category category;

    @Nullable
    private String img_url;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

    private Boolean updated;

    @Nullable
    private Integer totalLikes;

    private Map<String, ReactionDto> reactions;

    private Integer commentNumber;
}
