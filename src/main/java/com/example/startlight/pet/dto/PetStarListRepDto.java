package com.example.startlight.pet.dto;

import com.example.startlight.pet.entity.Edge;
import com.example.startlight.starList.dto.StarListRepDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PetStarListRepDto {
    private Long petId;
    private String petName;
    private String svgPath;
    private List<StarListRepDto> starList;
    private List<Edge> edges;
}
