package com.example.startlight.constellation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class ConstellationWithStarRepDto {
    private Long con_id;
    private String thumbnail_img;
    private List<StarNodeRepDto> nodes;
    private List<StarEdgeRepDto> edges;
}
