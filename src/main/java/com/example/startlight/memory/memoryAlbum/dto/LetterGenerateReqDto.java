package com.example.startlight.memory.memoryAlbum.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LetterGenerateReqDto {
    private String character;
    private String breed;
    private String pet_name;
    private String member_name;
    private String nickname;
}
