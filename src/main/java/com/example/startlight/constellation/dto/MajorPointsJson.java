package com.example.startlight.constellation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class MajorPointsJson {
    @JsonProperty("major_points")
    private List<List<Integer>> majorPoints;
}
