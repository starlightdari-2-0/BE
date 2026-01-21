package com.example.startlight.memory.memoryStar.dto;

import com.example.startlight.memory.memoryStar.entity.ActivityCtg;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class MemoryStarUpdateDto {

    private String name;

    private ActivityCtg activityCtg;

    private String content;

    private MultipartFile img_url;

    private Boolean shared;
}
