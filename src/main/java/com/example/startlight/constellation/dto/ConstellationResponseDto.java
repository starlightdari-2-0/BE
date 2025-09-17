package com.example.startlight.constellation.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ConstellationResponseDto {
    private Long con_id;
    private String code;
    private String thumbnail_img;
}
