package com.example.startlight.starList.mapper;

import com.example.startlight.pet.entity.Pet;
import com.example.startlight.starList.dto.StarListRepDto;
import com.example.startlight.starList.dto.StarListReqDto;
import com.example.startlight.starList.entity.StarList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface StarListMapper {
    StarListMapper INSTANCE = Mappers.getMapper(StarListMapper.class);

    @Mapping(source = "pet", target = "pet")
    StarList toEntity(StarListReqDto dto, Pet pet);

    @Mapping(source = "memoryStar.memory_id", target = "memory_id")
    StarListRepDto toDto(StarList entity);

    default List<StarList> toEntityList(List<StarListReqDto> dtoList, Pet pet) {
        return dtoList.stream()
                .map(dto -> toEntity(dto, pet))
                .collect(Collectors.toList());
    }

    default List<StarListRepDto> toDtoList(List<StarList> entityList) {
        return entityList.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
