package com.example.startlight.constellation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StarEdgeRequestDto {
    private Long edge_id;
    private Long con_id;
    private Long start_node_id;
    private Long end_node_id;
}
