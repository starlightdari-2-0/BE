package com.example.startlight.memoryStar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemoryStarSimpleRepDto {
    private Long memory_id;
    private String name;
    private String writer_name;
    private String img_url;
}
