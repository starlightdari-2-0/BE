package com.example.startlight.constellation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StarNodeRepDto {
    private Long star_node_id;
    private Long node_id;
    private Integer x_star;
    private Integer y_star;
}
