package com.example.startlight.memoryStar.dto;

import com.example.startlight.memoryStar.entity.ActivityCtg;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class MemoryStarReqDto {

    private Long star_node_id;

    private Long writer_id;

    private String writer_name;

    private Long pet_id;

    private String name;

    private ActivityCtg activityCtg;

    private String content;

    private MultipartFile img_url;

    private Boolean shared;

}
