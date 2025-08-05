package com.example.startlight.memoryStar.dto;

import com.example.startlight.memComment.dto.MemCommentRepDto;
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
