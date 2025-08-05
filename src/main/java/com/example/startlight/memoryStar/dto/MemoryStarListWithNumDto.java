package com.example.startlight.memoryStar.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MemoryStarListWithNumDto {
    List<MemoryStarSimpleRepDto> memoryStarSimpleRepDtoList;
    Integer memoryNumber;
}
