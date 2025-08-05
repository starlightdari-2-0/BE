package com.example.startlight.memoryStar.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemoryStarLikeDto {
    private Long memoryId;
    private Boolean isLiked;
    private Integer likes;
}
