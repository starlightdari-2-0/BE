package com.example.startlight.constellation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StarNodeRequestDto {
    private Long node_id;
    private Long con_id;
    private Integer x_star;
    private Integer y_star;
}
