package com.example.startlight.constellation.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StarNodeWithMemoryDto {
    private Long star_node_id;
    private Long node_id;
    private Integer x_star;
    private Integer y_star;
    @Builder.Default
    private Boolean written = false;
    private Long memory_id;

    public void setMemoryWritten(Long memory_id) {
        this.memory_id = memory_id;
        written = true;
    }
}
