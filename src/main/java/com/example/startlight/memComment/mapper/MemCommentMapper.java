package com.example.startlight.memComment.mapper;

import com.example.startlight.memComment.dto.MemCommentRepDto;
import com.example.startlight.memComment.entity.MemComment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MemCommentMapper {
    MemCommentMapper INSTANCE = Mappers.getMapper(MemCommentMapper.class);

    // ✅ Entity → DTO 변환 (`memoryStar.memory_id`를 `memory_id`로 매핑)
    @Mapping(source = "memoryStar.memory_id", target = "memory_id")
    MemCommentRepDto toDto(MemComment memComment);

    // ✅ DTO → Entity 변환 (memoryStar는 따로 설정해야 하므로 ignore)
    @Mapping(target = "memoryStar", ignore = true)
    MemComment toEntity(MemCommentRepDto memCommentRepDto);
}
