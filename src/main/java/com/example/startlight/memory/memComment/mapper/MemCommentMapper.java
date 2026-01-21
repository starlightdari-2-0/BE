package com.example.startlight.memory.memComment.mapper;

import com.example.startlight.memory.memComment.dto.MemCommentRepDto;
import com.example.startlight.memory.memComment.entity.MemComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemCommentMapper {
    MemCommentMapper INSTANCE = Mappers.getMapper(MemCommentMapper.class);

    @Mapping(source = "memoryStar.memory_id", target = "memory_id")
    MemCommentRepDto toDto(MemComment memComment);

    @Mapping(target = "memoryStar", ignore = true)
    MemComment toEntity(MemCommentRepDto memCommentRepDto);
}
