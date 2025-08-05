package com.example.startlight.starList.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StarListRepDto {
    private Long star_id;
    private Integer index_id;
    private Integer x_star;
    private Integer y_star;
    private Boolean written;
    private Long memory_id;
}
