package com.example.startlight.pet.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlaskResponseDto {
    @JsonProperty("svg_path")
    private String svgPath;

    @JsonProperty("edges")
    private List<List<Integer>> edges;

    @JsonProperty("major_points")
    private List<List<Integer>> majorPoints;
}
