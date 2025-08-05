package com.example.startlight.memoryStar.dto;

import com.example.startlight.memoryStar.entity.ActivityCtg;
import com.example.startlight.memoryStar.entity.EmotionCtg;
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

    private Long star_id;

    private Long writer_id;

    private String writer_name;

    private String name;

    private Long pet_id;

    private ActivityCtg activityCtg;

    private EmotionCtg emotionCtg;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedAt;

    private Boolean shared;

    private Boolean updated;

    private Integer likes;

    private Integer commentNumber;

    @Builder.Default
    private Boolean isLiked = false; // ✅ 기본값 설정

    private String img_url;
}
