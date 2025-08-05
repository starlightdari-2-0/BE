package com.example.startlight.memoryStar.mapper;

import com.example.startlight.memoryStar.dto.MemoryStarRepDto;
import com.example.startlight.memoryStar.dto.MemoryStarReqDto;
import com.example.startlight.memoryStar.dto.MemoryStarSimpleRepDto;
import com.example.startlight.memoryStar.entity.MemoryStar;
import com.example.startlight.starList.entity.StarList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MemoryStarMapper {
    MemoryStarMapper INSTANCE = Mappers.getMapper(MemoryStarMapper.class);

    @Mapping(source = "starList", target = "starList")
    @Mapping(target = "img_url", ignore = true)
    MemoryStar toEntity(MemoryStarReqDto dto, StarList starList);

    @Mapping(source = "starList.star_id", target = "star_id")
    MemoryStarRepDto toDto(MemoryStar memoryStar);

    MemoryStarSimpleRepDto toSimpleRepDto(MemoryStar memoryStar);
    List<MemoryStarSimpleRepDto> toSimpleRepDtoList(List<MemoryStar> memoryStars);
}
