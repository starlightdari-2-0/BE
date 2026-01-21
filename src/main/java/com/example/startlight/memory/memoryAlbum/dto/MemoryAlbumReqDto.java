package com.example.startlight.memory.memoryAlbum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MemoryAlbumReqDto {
    private Long pet_id;
    private String content;
    private List<String> images;
}
