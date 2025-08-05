package com.example.startlight.starList.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StarListReqDto {
    private Integer x_star;
    private Integer y_star;
}
