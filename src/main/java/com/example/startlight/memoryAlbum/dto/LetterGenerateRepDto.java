package com.example.startlight.memoryAlbum.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@NoArgsConstructor // 기본 생성자 추가
@AllArgsConstructor
@Jacksonized
public class LetterGenerateRepDto {
    @JsonProperty("letter")
    String letter;
    @JsonProperty("title")
    String title;
}
