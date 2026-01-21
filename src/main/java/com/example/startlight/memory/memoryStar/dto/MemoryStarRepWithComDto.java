package com.example.startlight.memory.memoryStar.dto;

import com.example.startlight.memory.memComment.dto.MemCommentRepDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class MemoryStarRepWithComDto {
    private MemoryStarRepDto memoryStarRepDto;
    private List<MemCommentRepDto> memComments;
}
