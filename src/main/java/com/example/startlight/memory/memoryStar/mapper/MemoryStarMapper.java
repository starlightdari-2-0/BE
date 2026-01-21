package com.example.startlight.memory.memoryStar.mapper;

import com.example.startlight.memory.memoryStar.dto.MemoryStarRepDto;
import com.example.startlight.memory.memoryStar.dto.MemoryStarReqDto;
import com.example.startlight.memory.memoryStar.dto.MemoryStarSimpleRepDto;
import com.example.startlight.memory.memoryStar.entity.MemoryStar;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MemoryStarMapper {
    MemoryStarMapper INSTANCE = Mappers.getMapper(MemoryStarMapper.class);

    @Mapping(target = "img_url", ignore = true)
    MemoryStar toEntity(MemoryStarReqDto dto);
    
    MemoryStarRepDto toDto(MemoryStar memoryStar);

    MemoryStarSimpleRepDto toSimpleRepDto(MemoryStar memoryStar);
    List<MemoryStarSimpleRepDto> toSimpleRepDtoList(List<MemoryStar> memoryStars);
}
