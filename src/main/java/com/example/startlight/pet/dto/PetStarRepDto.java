package com.example.startlight.pet.dto;

import com.example.startlight.constellation.dto.StarEdgeRepDto;
import com.example.startlight.constellation.dto.StarNodeWithMemoryDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class PetStarRepDto {
    private Long petId;
    private String petName;
    private String thumbnail_img;
    private List<StarNodeWithMemoryDto> nodes;
    private List<StarEdgeRepDto> edges;
}
