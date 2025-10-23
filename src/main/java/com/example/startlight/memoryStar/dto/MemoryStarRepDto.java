package com.example.startlight.memoryStar.dto;

import com.example.startlight.memoryStar.entity.ActivityCtg;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MemoryStarRepDto {
    private Long memory_id;

    private Long star_node_id;

    private Long writer_id;

    private String writer_name;

    private Long pet_id;

    private String name;

    private ActivityCtg activityCtg;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

    private Boolean shared;

    private Boolean updated;

    private Integer like1;

    private Integer like2;

    private Integer like3;

    private Integer totalLikes;

    private Integer commentNumber;

    @Builder.Default
    private Boolean isLiked1 = false;

    @Builder.Default
    private Boolean isLiked2 = false;

    @Builder.Default
    private Boolean isLiked3 = false;

    private String img_url;
}
