package com.example.startlight.post.dto;

import com.example.startlight.post.entity.Category;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class PostRequestDto {
    private String title;
    private String content;
    private Category category;

    @Nullable
    private Long funeral_id;

    @Nullable
    private MultipartFile image;
}
