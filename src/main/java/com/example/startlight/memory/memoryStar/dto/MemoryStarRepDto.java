package com.example.startlight.memory.memoryStar.dto;

import com.example.startlight.memory.memoryStar.entity.ActivityCtg;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

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

    private Integer totalLikes;

    private Map<String, ReactionDto> reactions;

    private Integer commentNumber;

    private String img_url;
}
