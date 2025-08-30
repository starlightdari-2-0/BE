package com.example.startlight.memoryAlbum.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LetterGeneratedFileRepDto {
    @JsonProperty("images")
    List<String> images;
    @JsonProperty("letter")
    String letter;
    @JsonProperty("title")
    String title;
}
