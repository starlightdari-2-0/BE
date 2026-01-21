package com.example.startlight.memory.memoryAlbum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LetterGenerateWithFileReqDto {
    private String character;
    private String breed;
    private List<String> texts;
    private Long pet_id;
    private String pet_name;
    private String member_name;
    private String nickname;
}
